package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

/**
 * @author Maynormoe
 */
public interface ReportService {
    /**
     * 获取日期区间营业额数据
     *
     * @param begin 开始
     * @param end   最后
     * @return TurnoverReportVO
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return UserReportVO
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     *  获取订单统计
      * @param begin 开始时间
     * @param end 结束时间
     * @return OrderReportVO
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计top10
      * @param begin 开始时间
     * @param end 结束时间
     * @return SalesTop10ReportVO
     */
    SalesTop10ReportVO getSalesTopStatistics(LocalDate begin, LocalDate end);
}
