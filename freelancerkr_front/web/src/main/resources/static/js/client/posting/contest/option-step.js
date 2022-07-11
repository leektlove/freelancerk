$(document).ready(function() {
    /*
    Wizard #5
    */
    var $w5finish = $('#w5').find('ul.pager li.finish'),
        $w5validator = $("#w5 form").validate({
            highlight: function(element) {
                $(element).closest('.form-group').removeClass('has-success').addClass('has-error');
            },
            success: function(element) {
                $(element).closest('.form-group').removeClass('has-error');
                $(element).remove();
            },
            errorPlacement: function( error, element ) {
                element.parent().append( error );
            }
        });

    $('#w5').bootstrapWizard({
        tabClass: 'wizard-steps',
        nextSelector: 'ul.pager li.next',
        previousSelector: 'ul.pager li.previous',
        firstSelector: null,
        lastSelector: null,
        onNext: function( tab, navigation, index, newindex ) {
            var validated = $('#w5 form').valid();
            if( !validated || !validateStep(index)) {
                $w5validator.focusInvalid();
                return false;
            }
        },
        onTabClick: function( tab, navigation, index, newindex ) {
            if (index === 1 && !checkPrizeInput()) {
                return false;
            }

            if ( newindex === index + 1 ) {
                return this.onNext( tab, navigation, newindex, null);
            } else if ( newindex > index + 1 ) {
                return false;
            } else {
                return true;
            }
        },
        onTabChange: function( tab, navigation, index, newindex ) {
            var $total = navigation.find('li').length - 1;
            $w5finish[ newindex != $total ? 'addClass' : 'removeClass' ]( 'hidden' );
            $('#w5').find(this.nextSelector)[ newindex == $total ? 'addClass' : 'removeClass' ]( 'hidden' );
        },
        onTabShow: function( tab, navigation, index ) {
            var $total = navigation.find('li').length - 1;
            var $current = index;
            var $percent = Math.floor(( $current / $total ) * 100);

            navigation.find('li').removeClass('active');
            navigation.find('li').eq( $current ).addClass('active');

            $('#w5').find('.progress-indicator').css({ 'width': $percent + '%' });
            tab.prevAll().addClass('completed');
            tab.nextAll().removeClass('completed');
        }
    });
})