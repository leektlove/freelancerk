(function($, window) {
    $.fn.replaceOptions = function(options) {
        var self, $option;

        this.empty();
        self = this;

        $.each(options, function(index, option){
            $option = $("<option></option>")
                .attr("value", option.value)
                .text(option.text);
            self.append($option);
        });
        this.prop('disabled', false);
    };

    $.fn.serializeFormJSON = function () {

        var o = {};
        var a = this.serializeArray();
        $.each(a, function () {
            if (o[this.name]) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
})(jQuery, window);

function readURL(input, elem, done) {

    /*<![CDATA[*/
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            if (elem) {
                elem.attr('src', e.target.result);
            }
            if (done) {
                done(reader.result);
            }
        }

        reader.readAsDataURL(input.files[0]);
    }
    /*]]>*/
}

function readURLAsBg(input, elem, done) {

    /*<![CDATA[*/
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            elem.css('background-image', 'url(' + e.target.result + ')');
            if (done) {
                done(reader.result);
            }
        }

        reader.readAsDataURL(input.files[0]);
    }
    /*]]>*/
}

function toBackHistory() {
    if (confirm("취소하시겠습니까?")) {
        location.href='javascript:history.back()';
    }
}