<div id="tabs">
	<ul>
	<li><a href="${ mainPath }/dashboard/ajax/list">Scheduler List</a></li>
	<li><a href="${ mainPath }/dashboard/ajax/create">Add Scheduler</a></li>
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