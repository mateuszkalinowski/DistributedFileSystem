package pl.dfs.distributedfilesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.expression.Lists;
import pl.dfs.distributedfilesystem.dataNodes.DataNode;

import java.lang.reflect.Array;
import java.util.ArrayList;

@SpringBootApplication
public class DistributedfilesystemApplication {

    @Bean
    ArrayList<DataNode> dataNodes() {
        ArrayList<DataNode> dataNodes = new ArrayList<>();

        try {
            dataNodes.add(new DataNode("localhost", 4444));
        }
        catch (Exception e) {

        }
        return dataNodes;
    }

	public static void main(String[] args) {
		SpringApplication.run(DistributedfilesystemApplication.class, args);
	}
}
