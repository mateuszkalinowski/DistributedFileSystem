package pl.dfs.distributedfilesystem.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pl.dfs.distributedfilesystem.files.FilesRepository;
import pl.dfs.distributedfilesystem.models.ToChangeRepository;


@Component
public class OnFinishLoading
        implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    FilesRepository filesRepository;

    @Autowired
    ToChangeRepository toChangeRepository;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        filesRepository.initializeDataNodesSizes();
        filesRepository.checkCohesion();
        filesRepository.tryToDelete();

        toChangeRepository.tryToExecuteCommands();
    }
}
