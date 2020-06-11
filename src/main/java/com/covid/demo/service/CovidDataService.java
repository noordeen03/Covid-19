package com.covid.demo.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.covid.demo.model.*;
@Service
public class CovidDataService {
	
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList();
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron="* * 1 * * *")
	public void fetchVirusData() throws ClientProtocolException, IOException {
		 List<LocationStats> newStats = new ArrayList();
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 HttpGet httpget = new HttpGet(VIRUS_DATA_URL);
		 HttpResponse httpresponse = httpclient.execute(httpget);
		 String data = EntityUtils.toString(httpresponse.getEntity());
		 //System.out.println(data);
		 StringReader csvReader = new StringReader(data);
		 Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
		 for (CSVRecord record : records) {
		    LocationStats locationstat = new LocationStats();
		    locationstat.setState(record.get("Province/State"));
		    locationstat.setCountry(record.get("Country/Region"));
		    locationstat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    int diffFromPreviousDate = Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2)); 
		    locationstat.setDiffFromPreviousDate(diffFromPreviousDate);
		   // System.out.println(locationstat);
		    newStats.add(locationstat);
		 }
		this.allStats = newStats; 
 	}
}
