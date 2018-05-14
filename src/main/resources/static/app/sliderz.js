$.each($('.sliderz'), function(index, obj){
	$(obj).slider({
		tooltip: 'hide',
		ticks: [-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
	    ticks_labels: ['Never', '', 'Very Rarely', '', 'Rarely', '', 'Sometimes', '', 'Often', '', 'Very Often']
	});
});