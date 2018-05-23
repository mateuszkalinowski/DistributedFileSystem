package pl.dfs.distributedfilesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.expression.Lists;
import pl.dfs.distributedfilesystem.dataNodes.DataNode;

import java.lang.reflect.Array;
import java.util.ArrayList;

@SpringBootApplication
public class DistributedfilesystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedfilesystemApplication.class, args);
	}
}
