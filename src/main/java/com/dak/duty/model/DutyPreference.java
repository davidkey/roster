package com.dak.duty.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DutyPreference{
	private Long dutyId;
	private Integer preference;
}