package com.thepapiok.multiplecard.controllers;

import com.thepapiok.multiplecard.services.ResultService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {
  private final ResultService resultService;

  @Autowired
  public ResultController(ResultService resultService) {
    this.resultService = resultService;
  }

  @PostMapping("/get_pages")
  public ResponseEntity<List<Integer>> getPages(@RequestParam int page, @RequestParam int maxPage) {
    return new ResponseEntity<>(resultService.getPages(page + 1, maxPage), HttpStatus.OK);
  }
}
