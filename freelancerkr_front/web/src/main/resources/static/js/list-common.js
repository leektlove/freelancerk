$(document).ready(function() {
    $('#dataTables').DataTable({
        responsive: true,
        "order": [[1, "desc"]],
        "stateSave": true
    });
});