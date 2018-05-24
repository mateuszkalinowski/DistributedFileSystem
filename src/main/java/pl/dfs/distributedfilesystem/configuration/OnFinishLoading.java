package pl.dfs.distributedfilesystem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.dfs.distributedfilesystem.files.FilesRepository;

@Component
public class OnFinishLoading
        implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    FilesRepository filesRepository;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        filesRepository.initializeDataNodesSizes();

        filesRepository.checkCohesion();

    }
}
