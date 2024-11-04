package org.example.serverb.controller;

import org.example.serverb.entity.Film;
import org.example.serverb.repository.FilmRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/filmsB")
public class FilmController {

    private final FilmRepository filmRepository;

    public FilmController(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @GetMapping("/")
    public List<Film> getFilmsB() {
        // Lấy danh sách phim từ Server B qua FilmService
        return filmRepository.findAll();
    }
}

