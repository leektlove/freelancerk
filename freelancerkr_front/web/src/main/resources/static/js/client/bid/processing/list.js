$(document).ready(function() {
    $('button.amount').click(function() {
        if($(this).hasClass('disable')) {
            $(this).removeClass('disable');
            $(this).removeClass('up');
            $(this).addClass('down');
        } else if ($(this).hasClass('down')) {
            $(this).removeClass('down');
            $(this).addClass('up');
         } else if ($(this).hasClass('up')) {
            $(this).addClass('disable');
            $(this).removeClass('down');
            $(this).removeClass('up');
        }
    });

    $('button.deadline').click(function() {
        if($(this).hasClass('disable')) {
            $(this).removeClass('disable');
            $(this).removeClass('up');
            $(this).addClass('down');
        } else if ($(this).hasClass('down')) {
            $(this).removeClass('down');
            $(this).addClass('up');
        } else if ($(this).hasClass('up')) {
            $(this).addClass('disable');
            $(this).removeClass('down');
            $(this).removeClass('up');
        }
    });

    $('button.bid').click(function() {
        if($(this).hasClass('disable')) {
            $(this).removeClass('disable');
            $(this).removeClass('up');
            $(this).addClass('down');
        } else if ($(this).hasClass('down')) {
            $(this).removeClass('down');
            $(this).addClass('up');
        } else if ($(this).hasClass('up')) {
            $(this).addClass('disable');
            $(this).removeClass('down');
            $(this).removeClass('up');
        }
    });
});

$(document).on('click', '#zoomIn', function() {
    if (currentZoom > 150) return;
    currentZoom = currentZoom + 10;
    $('.owl-item.active').find('img').css('width', currentZoom + '%');
});

$(document).on('click', '#zoomOut', function() {
    if (currentZoom < 10) return;
    currentZoom = currentZoom - 10;
    $('.owl-item.active').find('img').css('width', currentZoom + '%');
});

$(document).on('click', '#contestEntryFileFullBtn', function(e) {
    var id = $(e.target).data('id');
    window.open("/view/contest-entries/" + id + "/details", '_blank');
});

function modifyItem(projectId) {
    location.href = '/client/posting/project?id=' + projectId + '&mode=MODIFY';
}

function projectDetail(projectId, sufix) {
    location.href = '/client/project/' + projectId + '/details' + (sufix?sufix:'');
}

function contestDetail(contestId, sufix) {
    location.href = '/client/contest/' + contestId + '/details' + (sufix?sufix:'');
}

function goToPickView(contestId, numberOfApply) {
    if (parseInt(numberOfApply) === 0) {
        alert('제출된 작품이 없습니다. 작품이 제출되면 확인 후 선정하세요. ');
        return;
    }
    location.href = '/client/contest/' + contestId + '/pick';
}