package domain.videogamesshop.service;

import domain.videogamesshop.model.Platform;
import domain.videogamesshop.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    public Optional<Platform> findById(Long id){
        return platformRepository.findById(id);
    }

    public Platform save(Platform platform){
        return platformRepository.save(platform);
    }

    public void deleteById(Long id){
        platformRepository.deleteById(id);
    }
}
