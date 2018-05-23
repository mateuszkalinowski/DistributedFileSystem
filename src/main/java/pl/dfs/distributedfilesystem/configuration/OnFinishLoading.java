package pl.dfs.distributedfilesystem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.dfs.distributedfilesystem.dataNodes.DataNodes;
import pl.dfs.distributedfilesystem.filesDatabase.Files;

@Component
public class OnFinishLoading
        implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    Files files;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        files.initializeDataNodesSizes();
    }
}
