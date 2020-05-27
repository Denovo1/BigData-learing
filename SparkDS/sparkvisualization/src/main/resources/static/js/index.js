$(function () {
    //run sessionjob once
    $(".btnRun").click(function () {
        var taskid = $(this).parent().data("id");
        $.ajax({
            url: "/sparkapi/runJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                alert("reload");
                window.location.reload();
            }
        });
    });

    $(".btnPage").click(function () {
        var taskid = $(this).parent().data("id");
        $.ajax({
            url: "/sparkapi/runPageJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                alert("reload");
                window.location.reload();
            }
        });
    });

    $(".btnArea").click(function () {
        var taskid = $(this).parent().data("id");
        $.ajax({
            url: "/sparkapi/runAreaJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                alert("reload");
                window.location.reload();
            }
        });
    });

    $(".btnShow").click(function () {
        var taskid = $(this).parent().data("id");
        var taskStatus = $(this).parent().data("status");
        $.ajax({
            url: "/sessionshow/showJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                window.location.href = "/index/sessionindex?taskid=" + taskid;
            }
        });
    });

    $(".btnPageShow").click(function () {
        var taskid = $(this).parent().data("id");
        var taskStatus = $(this).parent().data("status");
        $.ajax({
            url: "/pageshow/showJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                window.location.href = "/index/pageindex?taskid=" + taskid;
            }
        });
    });

    $(".btnAreaShow").click(function () {
        var taskid = $(this).parent().data("id");
        $.ajax({
            url: "/areashow/showJob?taskid=" + taskid,
            type: "POST",
            data: {
                "taskid": $("#id_" + taskid).text()
            },
            dataType: "JSON",
            success: function (ret) {
                window.location.href = "/index/areaindex?taskid=" + taskid;
            }
        });
    });

    //delete job
    $(".btnDelete").click(function () {
        var taskid = $(this).parent().data("id");
        if (window.confirm("确定删除?")) {
            $.ajax({
                url: "/sparkapi/deleteJob?taskid=" + taskid,
                type: "POST",
                dataType: "JSON",
                data: {
                    "taskid": $("#id_" + taskid).text()
                },
                success: function (ret) {
                    alert("delete success!");
                    location.reload();
                }
            });
        }
    });

    // update
    $(".btnEdit").click(function () {
        $("#quartzModalLabel").html("cron edit");
        var taskid = $(this).parent().data("id");
        $("#edit_taskid").val(taskid);
        $("#edit_taskName").val($("#name_" + taskid).text());
        $("#edit_taskType").val($("#type_" + taskid).text());
        $("#edit_taskParam").val($("#param_" + taskid).text());

        $('#edit_taskid').attr("readonly", "readonly");

        $("#quartzModal").modal("show");
    });

    // create job
    $("#createBtn").click(function () {
        $("#quartzModalLabel").html("create job");
        $("#edit_taskid").val("");
        $("#edit_taskName").val("");
        $("#edit_taskType").val("");
        $("#edit_taskParam").val("");

        $('#edit_taskid').attr("readonly", "readonly");

        $("#quartzModal").modal("show");
    });

    $("#save").click(function () {
        $("#cancel").trigger('click');
        var taskid = $("#edit_taskid").val();
        $.ajax({
            url: "/sparkapi/saveOrUpdate?taskid=" + taskid,
            type: "POST",
            dataType: "JSON",
            data: {
                "taskid": $("#edit_taskid").val(),
                "taskName": $("#edit_taskName").val(),
                "taskType": $("#edit_taskType").val(),
                "taskParam": $("#edit_taskParam").val()
            },
            success: function (data) {
                alert("success!");
                window.parent.location.reload();
            }
        });
    });

    $("#cancel").click(function () {
        window.parent.location.reload();
    });


});