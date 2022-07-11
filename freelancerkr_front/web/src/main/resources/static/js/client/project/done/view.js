$(document).ready(function() {

});


function startProjectWithFreelancer(projectId) {
    let selectedBidItemId = $(`#collapse${projectId}one input:radio[name=bidItemForStartingProject]:checked`).val();
    if (!selectedBidItemId) {
        alert('프리랜서의 작품을 선택해 주세요');
        return;
    }
    location.href = '/client/directMarket/startWithSelectedSpecialist?projectBidId=' + selectedBidItemId;
}

function projectDetail(projectId, sufix) {
    location.href = '/client/project/' + projectId + '/details' + (sufix?sufix:'');
}

function contestDetail(contestId, sufix) {
    location.href = '/client/contest/' + contestId + '/details' + (sufix?sufix:'');
}

