/*
Name: 			Elements - Carousels - Examples
Written by: 	Okler Themes - (http://www.okler.net)
Theme Version:	7.0.0
*/

(function( $ ) {

	'use strict';

	/*
	Carousel
	*/
	$('#carousel').owlCarousel({
		loop: true,
		responsive: {
			0: {
				items: 2
			},
			479: {
				items: 2
			},
			768: {
				items: 2
			},
			979: {
				items: 3
			},
			1199: {
				items: 5
			}
		},
		navText: [],
		margin: 10,
		autoWidth: true,
		items: 4,
		rtl: ( $('html').attr('dir') == 'rtl' ) ? true : false
	});

	/*
	Videos
	*/
	$('#videos').owlCarousel({
		items:1,
		merge:true,
		loop:true,
		margin:10,
		video:true,
		lazyLoad:true,
		center:true,
		responsive:{
			480:{
				items:2
			},
			600:{
				items:4
			}
		},
		rtl: ( $('html').attr('dir') == 'rtl' ) ? true : false
	});

}).apply( this, [ jQuery ]);