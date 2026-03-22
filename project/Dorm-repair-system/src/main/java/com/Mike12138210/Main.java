package com.Mike12138210;

import com.Mike12138210.dao.RepairOrderMapper;
import com.Mike12138210.pojo.RepairOrder;
import com.Mike12138210.pojo.User;

import java.util.List;
import java.util.Scanner;

import com.Mike12138210.util.MyBatisUtil;
import com.Mike12138210.dao.UserMapper;
import org.apache.ibatis.session.SqlSession;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args){
        while(true){
            if(currentUser == null){
                ShowMainMenu();
            }else {
                if("student".equals(currentUser.getRole())){
                    ShowStudentMenu();
                }else if("admin".equals(currentUser.getRole())){
                    ShowAdminMenu();
                }
            }
        }
    }

    private static void ShowMainMenu(){
        System.out.println("===========================");
        System.out.println("\uD83C\uDFE0 宿舍报修管理系统");
        System.out.println("===========================");
        System.out.println("1.登录");
        System.out.println("2.注册");
        System.out.println("3.退出");

        loop:while(true){
            System.out.print("请选择操作（输入 1-3）：");
            String choice = sc.nextLine().trim();
            switch (choice){
                case "1":
                    while(!login()){
                    }
                    return;
                case "2":
                    register();
                    return;
                case "3":
                    System.exit(0); // break loop;
                default:
                    System.out.println("输入错误，请输入1,2或3。");
            }
        }
    }

    private static boolean login(){
        System.out.println("===== 用户登录 =====");

        System.out.print("请输入账号：");
        String account = sc.nextLine();

        System.out.print("请输入密码：");
        String password = sc.nextLine();

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);

            User user = mapper.selectByAccount(account);

            if(user == null){
                System.out.println("账号不存在，请重新登录。");
                return false;
            }
            if(!user.getPassword().equals(password)){
                System.out.println("密码错误，请重新登录。");
                return false;
            }

            currentUser = user;
            System.out.println("登录成功！角色：" + (user.getRole().equals("student") ? "学生" : "管理员"));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static void register(){
        while(true){
            System.out.println("===== 用户注册 =====");
            System.out.print("请选择角色（1-学生，2-维修人员）：");
            int roleChoice = sc.nextInt();
            sc.nextLine();

            String role = null;

            if(roleChoice == 1){
                role = "student";
            }else if(roleChoice == 2){
                role = "admin";
            }else{
                System.out.println("注册失败，请选择1或2，重新输入。");
                continue;
            }

            String account = null;
            if("student".equals(role)){
                System.out.print("请输入学号：");
                account = sc.nextLine().trim();
                if (!account.startsWith("3125") && !account.startsWith("3225")) {
                    System.out.println("学生账号必须以3125或3225开头，注册失败，请重新输入。");
                    continue;
                }
            }else{
                System.out.print("请输入工号：");
                account = sc.nextLine().trim();

                if(!account.startsWith("0025")){
                    System.out.println("管理员账号必须以0025开头，注册失败，请重新输入。");
                    continue;
                }
            }

            System.out.print("请输入密码：");
            String password = sc.nextLine();
            System.out.print("请确认密码：");
            String confirm = sc.nextLine();

            if(!password.equals(confirm)){
                System.out.println("两次密码不一致，注册失败，请重新输入。");
                continue;
            }

            try(SqlSession session = MyBatisUtil.getSqlSession()){
                UserMapper mapper = session.getMapper(UserMapper.class);

                User user = new User();
                user.setAccount(account);
                user.setPassword(password);
                user.setRole(role);

                int rows = mapper.insertUser(user);
                if(rows > 0){
                    System.out.println("注册成功！请返回主界面登录。");
                    break;
                }else{
                    System.out.println("注册失败，账号可能已存在，请重新输入。");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void ShowStudentMenu(){
        if(currentUser.getBuilding() == null || currentUser.getRoom() == null){
            System.out.println("首次登录，请先绑定宿舍");
            bindDorm();
        }

        while(true){
            System.out.println("===== 学生菜单 =====");
            System.out.println("1.绑定/修改宿舍");
            System.out.println("2.创建报修单");
            System.out.println("3.查看我的报修记录");
            System.out.println("4.取消报修单");
            System.out.println("5.修改密码");
            System.out.println("6.退出");
            System.out.println("====================");

            System.out.print("请选择操作（输入 1-6）：");
            String choice = sc.next().trim();
            switch (choice){
                case "1":
                    bindDorm();
                    break;
                case "2":
                    createOrder();
                    break;
                case "3":
                    listOrders();
                    break;
                case "4":
                    cancelOrder();
                    break;
                case "5":
                    changePassword();
                    break;
                case "6":
                    currentUser = null;
                    return;
                default:
                    System.out.println("输入错误，请输入1,2,3,4,5或6");
            }
        }
    }

    private static void bindDorm(){
        System.out.println("===== 绑定/修改宿舍 =====");
        System.out.print("请输入宿舍楼（如西4）：");
        String building = sc.nextLine().trim();
        System.out.print("请输入房间号（如101）：");
        String room = sc.nextLine().trim();

        currentUser.setBuilding(building);
        currentUser.setRoom(room);

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);

            int rows = mapper.updateUser(currentUser);
            if(rows > 0){
                System.out.println("宿舍信息更新成功！");
            }else{
                System.out.println("更新失败，请稍后重试。");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void createOrder() {
        System.out.println("===== 创建报修单 =====");

        System.out.print("请选择设备类型（如水龙头、灯泡等）：");
        String deviceType = sc.nextLine().trim();
        System.out.print("请填写问题描述：");
        String description = sc.nextLine().trim();

        RepairOrder order = new RepairOrder();
        order.setStudentId(currentUser.getId());
        order.setDeviceType(deviceType);
        order.setDescription(description);
        order.setStatus("等待中");

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);

            int rows = mapper.insertOrder(order);
            if(rows > 0){
                System.out.println("报修单创建成功！编号：" + order.getId());
            }else{
                System.out.println("创建失败，请稍后重试。");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void listOrders() {
        System.out.println("===== 我的报修记录 =====");

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            List<RepairOrder> orders = mapper.selectByStudentId(currentUser.getId());

            if(orders.isEmpty()){
                System.out.println("暂无报修记录。");
            }else{
                for(RepairOrder order : orders){
                    System.out.println("编号：" + order.getId());
                    System.out.println("设备类型：" + order.getDeviceType());
                    System.out.println("问题描述：" + order.getDescription());
                    System.out.println("状态：" + order.getStatus());
                    System.out.println("创建时间" + order.getCreateTime());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void cancelOrder() {
        System.out.println("===== 取消报修单 =====");

        System.out.print("请输入要取消的报修单ID：");
        int id = sc.nextInt();
        sc.nextLine();

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectById(id);

            if(order == null){
                System.out.println("报修单不存在。");
                return;
            }

            if(!order.getStudentId().equals(currentUser.getId())){
                System.out.println("你只能取消自己的报修单。");
                return;
            }

            if(!"等待中".equals(order.getStatus())){
                System.out.println("当前报修状态为：" + order.getStatus() + "，无法取消。");
                return;
            }

            order.setStatus("已取消");
            int row = mapper.updateOrder(order);
            if(row > 0){
                System.out.print("报修单已成功取消。");
                System.out.println("取消时间为" + order.getUpdateTime());
            }else{
                System.out.println("报修单取消失败，请稍后重试。");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void changePassword() {
        System.out.println("===== 修改密码 =====");

        System.out.print("请输入当前密码：");
        String oldPassword = sc.nextLine();

        if(!oldPassword.equals(currentUser.getPassword())){
            System.out.println("密码输入错误，请重新输入");
            return;
        }

        System.out.print("请输入新密码：");
        String newPassword = sc.nextLine();
        System.out.print("请再次输入新密码：");
        String confirmPassword = sc.nextLine();

        if(!confirmPassword.equals(newPassword)){
            System.out.println("两次密码输入不一致，请重新输入。");
            return;
        }

        currentUser.setPassword(newPassword);

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            int row = mapper.updateUser(currentUser);
            if(row > 0){
                System.out.println("密码修改成功！");
            }else{
                System.out.println("密码修改失败，请稍后重试。");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void ShowAdminMenu(){
        while(true){
            System.out.println("===== 管理员菜单 =====");
            System.out.println("1.查看所有报修单");
            System.out.println("2.查看报修单详情");
            System.out.println("3.更新报修单状态");
            System.out.println("4.删除报修单");
            System.out.println("5.修改密码");
            System.out.println("6.退出");
            System.out.println("====================");

            System.out.print("请选择操作（输入 1-6）：");
            String choice = sc.next().trim();
            switch (choice){
                case "1":
                    listAllOrders();
                    break;
                case "2":
                    viewOrderDetail();
                    break;
                case "3":
                    updateOrderStatus();
                    break;
                case "4":
                    deleteOrder();
                    return;
                case "5":
                    changePassword();
                    break;
                case "6":
                    currentUser = null;
                    return;
                default:
                    System.out.println("输入错误，请输入1,2,3,4,5或6。");
            }
        }
    }

    private static void listAllOrders() {
        System.out.println("===== 所有报修单 =====");

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            List<RepairOrder> orders = mapper.selectAll(null);

            if(orders.isEmpty()){
                System.out.println("暂无报修单。");
            }else{
                for(RepairOrder order : orders){
                    System.out.println("报修单ID：" + order.getId());
                    System.out.println("学生ID：" + order.getStudentId());
                    System.out.println("设备类型：" + order.getDeviceType());
                    System.out.println("问题描述：" + order.getDescription());
                    System.out.println("状态：" + order.getStatus());
                    System.out.println("创建时间" + order.getCreateTime());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void viewOrderDetail() {
        System.out.println("===== 查看报修单详情 =====");
        System.out.print("请输入所要查看报修单的ID：");
        int orderId = sc.nextInt();
        sc.nextLine();

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectById(orderId);

            if(order == null){
                System.out.println("未找到该报修单。");
                return;
            }

            System.out.print("------------------------");
            System.out.println("报修单ID：" + order.getId());
            System.out.println("学生ID：" + order.getStudentId());
            System.out.println("设备类型：" + order.getDeviceType());
            System.out.println("问题描述：" + order.getDescription());
            System.out.println("状态：" + order.getStatus());
            System.out.println("创建时间" + order.getCreateTime());
            System.out.println("最后修改时间：" + order.getUpdateTime());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void updateOrderStatus() {
        System.out.println("===== 更新报修单状态 =====");
        System.out.print("请输入报修单的ID：");
        int orderId = sc.nextInt();
        sc.nextLine();

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectById(orderId);

            if(order == null){
                System.out.println("未找到该报修单。");
                return;
            }

            System.out.print("请输入新状态(等待中、进行中、已完成 、已取消)：");
            String newStatus = sc.nextLine();

            if(order.getStatus().equals(newStatus)){
                System.out.println("该状态与当前报修单状态相同，更改失败。");
                return;
            }

            if(!"等待中".equals(newStatus) && !"进行中".equals(newStatus) && !"已完成".equals(newStatus) && !"已取消".equals(newStatus)){
                System.out.println("输入错误，请重新输入！");
                return;
            }

            order.setStatus(newStatus);
            int row = mapper.updateOrder(order);
            if(row > 0){
                System.out.println("状态更新成功！新状态为：" + order.getStatus());
            }else{
                System.out.println("状态更新失败，请稍后重试。");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void deleteOrder() {
        System.out.println("===== 删除报修单 =====");

        System.out.println("请输入要取消的报修单ID：");
        int orderId = sc.nextInt();
        sc.nextLine();
        System.out.print("确认删除？（Y/N):");
        String confirm = sc.nextLine();
        if(!"Y".equals(confirm)){
            System.out.println("取消删除。");
            return;
        }

        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);

            int row = mapper.deleteOrder(orderId);
            if(row > 0){
                System.out.println("删除成功！");
                return;
            }else {
                System.out.println("删除失败，可能该报修单不存在，请重新确认。");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}