$(document).ready(function() {
    $('span[data-ajax-on-modal]').magnificPopup({
        type: 'ajax',
        tLoading: '<div class="bounce-loader"><div class="bounce1"></div><div class="bounce2"></div><div class="bounce3"></div></div>',
        mainClass: 'portfolio-ajax-modal',
        closeBtnInside: true,
        gallery: {
            enabled: true
        },
        callbacks: {
            ajaxContentAdded: function() {

                // Wrapper
                var $wrapper = $('.portfolio-ajax-modal');

                // Close
                $wrapper.find('a[data-ajax-portfolio-close]').on('click', function(e) {
                    e.preventDefault();
                    $.magnificPopup.close();
                });

                // Remove Next and Close
                if($('div[data-ajax-on-modal]').length <= 1) {

                    $wrapper.find('a[data-ajax-portfolio-prev], a[data-ajax-portfolio-next]').remove();

                } else {

                    // Prev
                    $wrapper.find('a[data-ajax-portfolio-prev]').on('click', function(e) {
                        e.preventDefault();
                        $('.mfp-arrow-left').trigger('click');
                        return false;
                    });

                    // Next
                    $wrapper.find('a[data-ajax-portfolio-next]').on('click', function(e) {
                        e.preventDefault();
                        $('.mfp-arrow-right').trigger('click');
                        return false;
                    });

                }

                // Carousel
                $(function() {
                    $('[data-plugin-carousel]:not(.manual), .owl-carousel:not(.manual)').each(function() {
                        var $this = $(this),
                            opts;

                        var pluginOptions = theme.fn.getOptions($this.data('plugin-options'));
                        if (pluginOptions)
                            opts = pluginOptions;

                        $this.themePluginCarousel(opts);
                    });
                });

            }
        }
    });
});