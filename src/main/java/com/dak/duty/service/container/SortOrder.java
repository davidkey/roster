package com.dak.duty.service.container;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dak.duty.model.Duty;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SortOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private Long id;

	@JsonProperty("sortOrder")
	private Integer sortOrder;

	public static Map<Long, Integer> getSortMap(@NonNull final List<SortOrder> sortOrders) {
		return sortOrders.stream().collect(Collectors.toMap(SortOrder::getId, SortOrder::getSortOrder));
	}
	
	public static SortOrder fromDuty(final Duty duty) {
		return new SortOrder(duty.getId(), duty.getSortOrder());
	}

	/**
	 * No constructor; breaks jackson marshaling!
	 * https://stackoverflow.com/questions/22102697/spring-mvc-bad-request-with-requestbody
	 */
}
