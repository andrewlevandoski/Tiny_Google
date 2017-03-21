(function() {
  $('input[type=text]').click(function() {
    $('input[type=file]').trigger('click');
  });

  $('input[type=file]').change(function() {
      $('input[type=text]').val($(this).val());
  });
  
  // Magic!
  console.log('Keepin\'n it clean with an external script!');

  $('.matches').hide();

  // Get input and pass on to getJSON()
  $('.flexsearch-input').keyup(function(event) {
    var input = $(".flexsearch-input").val();
    $('.matches').html("");
    $('.matches').show();

    getJSON(input);
  });

  // Actual functionality
  function getJSON(input) {
    $.ajax({
      url:"http://www.mattbowytz.com/simple_api.json?data=all",
      type:"GET",
      dataType:"json"
    })

    .done(function(json) {
      json.data.programming.forEach(function(search) {
        search = search.toLowerCase();

        if (input.length > 0 && search.startsWith(input.toLowerCase())) {
          $('.matches').append("<a target=\"_blank\" href=\"http://www.google.com/search?q=" + search + "\">" + search + "</a>");
        }
      });

      json.data.interests.forEach(function(search) {
        search = search.toLowerCase();

        if (input.length > 0 && search.startsWith(input.toLowerCase())) {
          $('.matches').append("<a target=\"_blank\" href=\"http://www.google.com/search?q=" + search + "\">" + search + "</a>");
        }
      });
    })

    .fail(function() {
      console.log("Error");
    });
  }
})();
