package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author Maynormoe
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private WorkspaceService workspaceService;


    /**
     * 获取日期区间营业额数据
     *
     * @param begin 开始
     * @param end   最后
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 封装日期数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        String dateListString = StringUtils.join(dateList, ",");
        // 封装营业额
        ArrayList<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            // 查询date日期对应的营业额数据
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // select sum(amount) from orders where orderTime > beginTime and orderTime < endTime status = 5
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("begin", beginTime);
            hashMap.put("end", endTime);
            hashMap.put("status", Orders.CANCELLED);
            Double turnover = orderMapper.sumByMap(hashMap);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        });
        String turnoverListString = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder().dateList(dateListString).turnoverList(turnoverListString).build();
    }

    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return UserReportVO
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //新增用户数量 select count(id) from user where create_time < ? and createTime > ?
        List<Integer> newUserList = new ArrayList<>();
        // 总用户数量 select count(id) from user where  createTime < ?
        List<Integer> totalUserList = new ArrayList<>();

        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap<String, Object> map = new HashMap<>();
            map.put("end", endTime);

            Integer totalUserCount = userMapper.countByMap(map);
            totalUserList.add(totalUserCount);
            map.put("begin", beginTime);
            Integer newUserCount = userMapper.countByMap(map);
            newUserList.add(newUserCount);
        });

        // 封装vo
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 获取订单统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return OrderReportVO
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 存放日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 存放订单信息
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> validOrderList = new ArrayList<>();


        // 遍历dataList集合 查询每天有效订单数 和总订单数
        dateList.forEach(date -> {
            // 查询每天订单总数 select count(id) from orders where order_time > ? and order_time <?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderTotalCount = getOrderCount(beginTime, endTime, null);


            // 查询每天有效订单 select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer orderValidCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            // 保存
            totalOrderList.add(orderTotalCount);
            validOrderList.add(orderValidCount);
        });

        //计算时间内的订单总数
        Integer totalOrderCount = totalOrderList.stream().reduce(Integer::sum).get();
        // 计算订单有效数量
        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            // 计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .build();
    }

    /**
     * 统计top10
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return SalesTop10ReportVO
     */
    @Override
    public SalesTop10ReportVO getSalesTopStatistics(LocalDate begin, LocalDate end) {
        // select  od.name sum(od.number) from order_detail od , orders o where od.orderid = o.id and o.order_time > ? and order_time < ? and status = 5 group by od.name order by number desc limit 0, 10
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTopList = orderMapper.getSalesTop(beginTime, endTime);
        List<String> names = salesTopList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTopList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String nameList = StringUtils.join(names, ",");
        String numberList = StringUtils.join(numbers, ",");


        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出运营数据报表
     *
     * @param response res
     */
    @Override
    public void exportBussinessData(HttpServletResponse response) {
        // 查询数据库 获取营业数据
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);

        LocalDateTime beginTime = LocalDateTime.of(beginDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        // 通过PoI将数据写入Excel
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");


        try {
            // 基于模板文件创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            // 填充数据
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            // 湖区第二行
            XSSFRow row = sheet1.getRow(1);

            row.getCell(1).setCellValue("时间" + beginDate + "至" + endDate);
            sheet1.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet1.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet1.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());

            // 获取第五行
            sheet1.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet1.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
            // 明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO businessDataVo = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow row1 = sheet1.getRow(7 + i);
                row1.getCell(1).setCellValue(date.toString());
                row1.getCell(2).setCellValue(businessDataVo.getTurnover());
                row1.getCell(3).setCellValue(businessDataVo.getValidOrderCount());
                row1.getCell(4).setCellValue(businessDataVo.getOrderCompletionRate());
                row1.getCell(5).setCellValue(businessDataVo.getUnitPrice());
                row1.getCell(6).setCellValue(businessDataVo.getNewUsers());

            }
            // 通过输出流将excel下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            // close
            outputStream.close();
            excel.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }


    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }
}
