package com.dak.duty.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class VersionServiceKotlin (
	@Value("\${info.build.version:}") private val version : String,
	@Value("\${git.commit.id:}") private val commitId : String,
	@Value("\${info.build.timestamp:}") private val timestamp : String
){

	fun getVersion(): String = this.version
	fun getCommitId(): String = this.commitId
	fun getTimestamp(): String = this.timestampd
	
}