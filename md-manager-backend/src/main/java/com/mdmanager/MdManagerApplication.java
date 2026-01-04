package com.mdmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Markdown文件管理系统启动类
 *
 * <p>这是Spring Boot应用程序的入口类，负责启动整个应用</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.mdmanager.mapper")  // 扫描Mapper接口所在的包
public class MdManagerApplication {

    /**
     * 应用程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MdManagerApplication.class, args);
        System.out.println("====================================");
        System.out.println("  Markdown文件管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("====================================");
    }
}
