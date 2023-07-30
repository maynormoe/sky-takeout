package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @author Maynormoe
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据相关接口")
@Slf4j
public class ReportController {


    @Autowired
    private ReportService reportService;


    /**
     * 营业额统计
     *
     * @param begin 开始
     * @param end   结束
     * @return Result<TurnoverReportVO>
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }


    /**
     * 用户数据统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return Result<UserReportVO>
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户数据统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户数据统计：{}，{}", begin, end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
        return Result.success(userReportVO);
    }

    /**
     * 订单数据统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return Result<OrderStatisticsVO>
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数据统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单数据统计：{}，{}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrderStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    /**
     * 销量排名
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return Result<OrderStatisticsVO>
     */
    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<SalesTop10ReportVO> salesTopStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量排名：{}，{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTopStatistics(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 导出运营数据报表
     *
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("导出运营数据报表")
    public void export(HttpServletResponse response) {
        reportService.exportBussinessData(response);
    }
}
