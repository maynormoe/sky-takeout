package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Maynormoe
 */
@RestController("OrderAdminController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
public class OrderController {


    @Autowired
    private OrderService orderService;


    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO ordersPageQueryDTO
     * @return Result<OrderVO>
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("分页查询")
    public Result<PageResult<OrderVO>> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询：{}", ordersPageQueryDTO);
        PageResult<OrderVO> pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return Result<OrderStatisticsVO>
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getStatistics() {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     *
     * @return Result<OrderDetail>
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getDetail(@PathVariable Long id) {
        log.info("查询订单详情 id{}", id);
        OrderVO orderVO = orderService.detail(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     *
     * @return Result<OrderVO>
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<OrderVO> confirm(@RequestBody OrdersDTO ordersDTO) {
        log.info("接单id:{}", ordersDTO.getId());
        orderService.confirm(ordersDTO.getId());
        return Result.success();
    }

    /**
     * 拒单
     *
     * @param ordersRejectionDTO 拒单传输对象
     * @return Result<OrderVO>
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<OrderVO> reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("拒单id:{}, 原因: {}", ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     *
     * @param ordersCancelDTO ordersCancel
     * @return Result<OrderVO>
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<OrderVO> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        log.info("取消订单id:{}, 原因: {}", ordersCancelDTO.getId(), ordersCancelDTO.getCancelReason());
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }


    /**
     * 派送
     *
     * @param id id
     * @return Result<OrderVO>
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("取消订单")
    public Result<OrderVO> delivery(@PathVariable Long id) {
        log.info("派送id: {}", id);
        orderService.delivery(id);
        return Result.success();
    }


    /**
     * 完成订单
     *
     * @return Result<String>
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单接口")
    public Result<String> complete(@PathVariable Long id) {
        log.info("订单完成 id: {}", id);
        orderService.complete(id);
        return Result.success();
    }



}
