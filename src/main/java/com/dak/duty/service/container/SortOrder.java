package com.dak.duty.service.container;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

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

	public static HashMap<Long, Integer> getSortMap(@NonNull final List<SortOrder> sortOrders) {
		final HashMap<Long, Integer> sortMap = new HashMap<>(sortOrders.size());

		for (final SortOrder so : sortOrders) {
			sortMap.put(so.getId(), so.getSortOrder());
		}

		return sortMap;
	}
	
	public static SortOrder fromDuty(final Duty duty) {
		return new SortOrder(duty.getId(), duty.getSortOrder());
	}

	/**
	 * No constructor; breaks jackson marshaling!
	 * https://stackoverflow.com/questions/22102697/spring-mvc-bad-request-with-requestbody
	 */
}
