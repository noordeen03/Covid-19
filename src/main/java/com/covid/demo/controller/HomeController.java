package com.covid.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.covid.demo.model.LocationStats;
import com.covid.demo.service.CovidDataService;

@Controller
public class HomeController {
	
	@Autowired
	CovidDataService covidDataService;
	
	@GetMapping("/")
	public String Home(Model model) {
		List<LocationStats> allstats = covidDataService.getAllStats();
		int totalReportedCases = allstats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalReportedCasesAsPreviousDay = allstats.stream().mapToInt(stat -> stat.getDiffFromPreviousDate()).sum();
		model.addAttribute("locationStats",allstats);
		model.addAttribute("totalReportedCases",totalReportedCases);
		model.addAttribute("totalReportedCasesAsPreviousDay",totalReportedCasesAsPreviousDay);
		return "home";
	}
	
}
