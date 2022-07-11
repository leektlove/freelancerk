var prizeFor1stElem;
var prizeFor2ndElem;
var prizeFor3rdElem;

$(document).ready(function() {

    prizeFor1stElem = $('input[name=prizeFor1st]');
    prizeFor2ndElem = $('input[name=prizeFor2nd]');
    prizeFor3rdElem = $('input[name=prizeFor3rd]');

    $('input[type=radio][name=pickType]').change(function() {
        var pickedValue = $(this).val();

        if (pickedValue === '1') {
            $('.container-1st').hide();
            $('.container-2nd').hide();
            $('.container-3rd').hide();
        } else if (pickedValue === '2') {
            $('.container-1st').show();
            $('.container-2nd').show();
            $('.container-3rd').hide();
        } else if (pickedValue === '3') {
            $('.container-1st').show();
            $('.container-2nd').show();
            $('.container-3rd').show();
        }
        $('.prize-input-container input').val('');
    });

    $('#prizeModificationForm').submit(function(e) {
        e.preventDefault();

        if (prizeModified) {
            alert('1회의 수정을 완료하셨기 때문에 더 이상의 상금 수정은 불가능합니다.');
            return false;
        }

        if (!checkPrizeBeforeModification()) {
            return false;
        }

        if (!confirm('이대로 수정하시겠습니까? 더 이상의 상금 수정은 불가능합니다.')) {
            return false;
        }

        if (!checkPrize()) {
            return false;
        }

        var prizeFor1st = removeComma($('input[name=prizeFor1st]').val());
        var prizeFor2nd = removeComma($('input[name=prizeFor2nd]').val());
        var prizeFor3rd = removeComma($('input[name=prizeFor3rd]').val());

        if (isNaN(prizeFor1st)) {
            prizeFor1st = 0;
        }
        if (isNaN(prizeFor2nd)) {
            prizeFor2nd = 0;
        }
        if (isNaN(prizeFor3rd)) {
            prizeFor3rd = 0;
        }

        $('input[name=prizeFor1st]').val(prizeFor1st);
        $('input[name=prizeFor2nd]').val(prizeFor2nd);
        $('input[name=prizeFor3rd]').val(prizeFor3rd);

        $.ajax({
            type: 'POST',
            url: $(this).attr('action'),
            data: $('#prizeModificationForm').serialize(),
            success: function (data) {
                alert('수정되었습니다.');
                location.reload();
            },
            error: function (error) {
                alert('서버와의 통신 중 문제가 발생하였습니다.');
            }
        });

    });

    prizeFor1stElem.change(function() {
        checkPrize();
    });

    prizeFor2ndElem.change(function() {
        checkPrize();
    });

    prizeFor3rdElem.change(function() {
        checkPrize();
    });
});

function pickEntries() {
    var entryIds = [];

    $('input:checkbox[name=color]:checked.order1').each(function(){entryIds.push($(this).val())});

    if (entryIds.length < 1) {
        alert('먼저 작품을 클릭해서 순위를 확정하세요.');
        return;
    }

    if (!confirm('이대로 확정하시겠습니까? 확정 후에는 당선자와 자유롭게 커뮤니케이션 하시며 작품에 대한 수정/보완을 하실 수 있습니다.')) {
        return;
    }

    if (limit === 3) {
        $('input:checkbox[name=color]:checked.order2').each(function(){entryIds.push($(this).val())});
        $('input:checkbox[name=color]:checked.order3').each(function(){entryIds.push($(this).val())});

        if (entryIds.length < 3) {
            alert(limit + '위까지의 순위를 정해주세요. 순위 또는 금액을 수정하실 수도 있습니다.');
            return;
        }
    }

    if (limit === 2) {
        $('input:checkbox[name=color]:checked.order2').each(function(){entryIds.push($(this).val())});

        if (entryIds.length < 2) {
            alert(limit + '위까지의 순위를 정해주세요. 순위 또는 금액을 수정하실 수도 있습니다.');
            return;
        }
    }

    if (limit === 1) {

        if (entryIds.length < 1) {
            alert(limit + '위까지의 순위를 정해주세요. 순위 또는 금액을 수정하실 수도 있습니다.');
            return;
        }
    }

    var id = $('input[name=id]').val();
    var formData = new FormData();
    for (var i = 0; i < entryIds.length; i++) {
        formData.append('contestEntryId[]', entryIds[i]);
    }
    $.ajax({
        type: 'POST',
        url: '/contests/' + id + '/picks',
        data: formData,
        processData: false,
        cache: false,
        contentType: false,
        success: function(result) {
            alert('축하합니다! 앞으로 1주일 간 선정하신 작품에 대한 수정 및 보완 작업을 완료하신 후 상금을 지급하시면 컨테스트가 종료됩니다. 작업자와 커뮤니케이션 해보세요!');
            location.href = '/client/project/processingList';
        },
        error: function() {
            alert('요청에 실패했습니다. 문제가 계속될 경우 고객센터로 문의 주세요.');
        }
    })
}

function checkPrizeBeforeModification() {
    var pickType = $('input[type=radio][name=pickType]:checked').val();
    var prizeFor1st = 1*removeComma(prizeFor1stElem.val());
    var prizeFor2nd = 1*removeComma(prizeFor2ndElem.val());
    var prizeFor3rd = 1*removeComma(prizeFor3rdElem.val());

    if (pickType === '3') {
        if (totalPrize !== (prizeFor1st + prizeFor2nd + prizeFor3rd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if ((prizeFor3rd > prizeFor2nd) || (prizeFor3rd > prizeFor1st)) {
            $('.prize-input-container input').val('');
            alert('3위 상금은 1위 또는 2위상금보다 높을 수 없습니다.');
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위상금보다 높을 수 없습니다.');
            return false;
        }
    } else if (pickType === '2') {
        if (totalPrize !== (prizeFor1st + prizeFor2nd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위 상금보다 높을 수 없습니다.');
            prizeFor1stElem.focus();
            return false;
        }
    } else if (pickType === '1') {
        prizeFor1stElem.val(prizeFor1stElem);
        prizeFor1st = 1*removeComma(prizeFor1stElem.val());
        if (prizeFor1st === 0) {
            alert('금액을 입력해 주세요');
            return false;
        }
    }

    return true;
}

function checkPrize() {
    var pickType = $('input[type=radio][name=pickType]:checked').val();
    var prizeFor1st = 1*removeComma(prizeFor1stElem.val());
    var prizeFor2nd = 1*removeComma(prizeFor2ndElem.val());
    var prizeFor3rd = 1*removeComma(prizeFor3rdElem.val());
    if (!prizeFor1st) {
        prizeFor1st = 0;
    }
    if (!prizeFor2nd) {
        prizeFor2nd = 0;
    }
    if (!prizeFor3rd) {
        prizeFor3rd = 0;
    }
    if (pickType === '3') {
        if ((prizeFor1st && prizeFor2nd && prizeFor3rd) && totalPrize != (prizeFor1st + prizeFor2nd + prizeFor3rd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if ((prizeFor3rd > prizeFor2nd) || (prizeFor3rd > prizeFor1st)) {
            $('.prize-input-container input').val('');
            alert('3위 상금은 1위 또는 2위상금보다 높을 수 없습니다.');
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위상금보다 높을 수 없습니다.');
            return false;
        }

        if (totalPrize == (prizeFor1st + prizeFor2nd + prizeFor3rd) && !prizeFor3rd && prizeFor2nd) {
            $('input[type=radio][name=pickType][value=2]').prop('checked', true);
            $('.container-2nd').show();
            $('.container-3rd').hide();
        } else if (totalPrize == (prizeFor1st + prizeFor2nd + prizeFor3rd) && !prizeFor3rd && !prizeFor2nd) {
            $('input[type=radio][name=pickType][value=1]').prop('checked', true);
            $('.container-2nd').hide();
            $('.container-3rd').hide();
        }
    } else if (pickType === '2') {
        if ((prizeFor1st && prizeFor2nd) && totalPrize != (prizeFor1st + prizeFor2nd)) {
            $('.prize-input-container input').val('');
            alert('금액 합계가 안맞습니다. 다시 입력해 주세요.');
            prizeFor1stElem.focus();
            return false;
        }

        if (prizeFor2nd > prizeFor1st) {
            $('.prize-input-container input').val('');
            alert('2위 상금은 1위 상금보다 높을 수 없습니다.');
            prizeFor1stElem.focus();
            return false;
        }

        if (totalPrize === (prizeFor1st + prizeFor2nd) && !prizeFor2nd) {
            $('input[type=radio][name=pickType][value=1]').prop('checked', true);
            $('.container-2nd').hide();
            $('.container-3rd').hide();
        }
    } else if (pickType === '1') {
        prizeFor1stElem.val(prizeFor1stElem);
        prizeFor1st = 1*removeComma(prizeFor1stElem.val());
        if (prizeFor1st === 0) {
            alert('금액을 입력해 주세요');
            return false;
        }
    }

    return true;
}

function showModal() {
    if (prizeModified) {
        alert('1회의 수정을 완료하셨기 때문에 더 이상의 상금 수정은 불가능합니다.');
        return;
    }

    $('#edit_reward').modal('show');
}