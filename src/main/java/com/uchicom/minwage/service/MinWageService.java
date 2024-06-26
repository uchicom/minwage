// (C) 2024 uchicom
package com.uchicom.minwage.service;

import com.uchicom.minwage.enumeration.Prefecture;
import com.uchicom.minwage.model.MinWage;
import com.uchicom.minwage.model.MinWageCreator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class MinWageService {

  private final PdfService pdfService;
  private final MinWageCreator minWageCreator;

  @Inject
  public MinWageService(PdfService pdfService, MinWageCreator minWageCreator) {
    this.pdfService = pdfService;
    this.minWageCreator = minWageCreator;
  }

  public MinWage getMinWage() throws IOException, InterruptedException {
    var text = pdfService.getPdfText();
    var cleanList = clean(text);
    var relocationList = relocation(cleanList);
    return minWageCreator.createMinWage(relocationList);
  }

  List<String> clean(String text) {
    var list = new ArrayList<String>(1024);
    for (var line : text.split("\n", 0)) {
      if (line.startsWith("年")) {
        continue;
      }
      if (line.startsWith("都")) {
        continue;
      }
      if (line.startsWith("全")) {
        continue;
      }
      if (line.startsWith("改")) {
        continue;
      }
      if (line.startsWith("平")) {
        continue;
      }
      if (line.startsWith("令")) {
        continue;
      }
      if (line.contains("-")) {
        continue;
      }
      list.add(line.replaceAll(" |,", "").replace("元", "1"));
    }
    return list;
  }

  List<String> relocation(List<String> list) {
    var prefectureSize = getPprefectureSize();
    var cleanList = new ArrayList<String>(list.size());
    cleanList.addAll(list.subList(0, prefectureSize));
    for (var i = prefectureSize; i < list.size(); i++) {
      var line = list.get(i);
      if (line.endsWith("日")) {
        cleanList.add(list.get(i - prefectureSize) + line);
      }
    }
    return cleanList;
  }

  int getPprefectureSize() {
    return Prefecture.values().length;
  }
}
