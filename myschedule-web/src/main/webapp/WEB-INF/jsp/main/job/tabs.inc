<div id="tabs">
	<ul>
	<li><a href="${ mainPath }/job/ajax/list-trigger-jobs">Jobs with trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-no-trigger-jobs">Jobs without trigger</a></li>
	<li><a href="${ mainPath }/job/ajax/list-executing-jobs">Currently Executing Jobs</a></li>
	</ul>
	<div id="tabs-1"></div>
</div>
<script>
$(document).ready(function() {
	$("#tabs").tabs({
		ajaxOptions: {
			error: function( xhr, status, index, anchor ) {
				$( anchor.hash ).html("Failed to load content." );
			}
		}
	});
});
</script>