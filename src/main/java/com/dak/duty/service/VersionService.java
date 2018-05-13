package com.dak.duty.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
@Getter
public class VersionService {

	private final String version;
	private final String commitId;
	private final String timestamp;
	
	public VersionService(
			@Value("${info.build.version:}") final String version, 
			@Value("${git.commit.id:}") final String commitId,
			@Value("${info.build.timestamp:}") final String timestamp) {
		this.version = version;
		this.commitId = commitId;
		this.timestamp = timestamp;
	}

}
