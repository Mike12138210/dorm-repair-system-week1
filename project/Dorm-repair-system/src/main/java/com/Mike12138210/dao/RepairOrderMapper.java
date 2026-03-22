package com.Mike12138210.dao;

import com.Mike12138210.pojo.RepairOrder;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RepairOrderMapper {
    // 插入新的报修单
    int insertOrder(RepairOrder order);

    // 根据学生ID查询该学生的所有报修单
    List<RepairOrder> selectByStudentId(Integer studentId);

    // 根据报修单ID查询单个报修单
    RepairOrder selectById(Integer id);

    //更新报修单
    int updateOrder(RepairOrder order);

    // 管理员：查询所有报修单，可按照状态筛选（status 为 null 则查询所有）
    List<RepairOrder> selectAll(@Param("status") String status);

    // 管理员：删除报修单
    int deleteOrder(Integer id);
}
