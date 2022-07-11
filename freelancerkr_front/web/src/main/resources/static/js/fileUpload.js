$(document).ready(function(){
    var fileTarget = $('.filebox .upload_hidden');
    var filename;
    fileTarget.on('change', function(){
        if(window.FileReader){
            // 파일명 추출
            filename = $(this)[0].files[0].name;
        }

        else {
            // Old IE 파일명 추출
            filename = $(this).val().split('/').pop().split('\\').pop();
        };

        var $newInputFile = $('<input/>').attr('type', 'file').attr('name', 'referenceFile[]');
        $newInputFile.files = $(this)[0].files;
        $newInputFile.css('display', 'none');
        $(this).append(
            $newInputFile
        );

        $(this).siblings('.upload-name').val(filename);
    });

    //preview image
    var imgTarget = $('.preview-image .upload_hidden');

    imgTarget.on('change', function(){
        var parent = $(this).parent();
        parent.children('.upload-display').remove();

        if(window.FileReader){
            var reader = new FileReader();
            reader.onload = function(e){
                var src = e.target.result;
                var filenameDiv = '<span class="upload-display p-2 mr-2">';
                filenameDiv += '<span class="upload-thumb-wrap">'+ filename;
                filenameDiv += '<button id="" type="button" class="btn btn-primary remove">X</button>';
                filenameDiv += '</span></span>';
                $('#preview_filename').append(filenameDiv);
            }
            reader.readAsDataURL($(this)[0].files[0]);
        }

        else {
            $(this)[0].select();
            $(this)[0].blur();
            var imgSrc = document.selection.createRange().text;
            parent.prepend('<div class="upload-display"><div class="upload-thumb-wrap"><img class="upload-thumb"></div></div>');

            var img = $(this).siblings('.upload-display').find('img');
            img[0].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enable='true',sizingMethod='scale',src=\""+imgSrc+"\")";
            }
        });
    });
