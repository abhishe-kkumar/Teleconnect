// Small convenience scripts. The real validation and recharge logic runs in Java on the server.
document.addEventListener('DOMContentLoaded', function () {

  // Plans page: allow 2 to 3 plans to be compared.
  var picks = document.querySelectorAll('input[name="ids"]');
  var button = document.getElementById('compare-btn');
  var hint = document.getElementById('compare-hint');

  function updateCompare() {
    var chosen = 0;
    picks.forEach(function (box) { if (box.checked) chosen++; });
    picks.forEach(function (box) { box.disabled = !box.checked && chosen >= 3; });
    if (button) { button.disabled = chosen < 2; }
    if (hint) {
      hint.textContent = chosen < 2 ? 'Select at least 2 plans'
        : chosen + ' selected' + (chosen === 3 ? ' (maximum)' : '');
    }
  }
  if (picks.length > 0) {
    picks.forEach(function (box) { box.addEventListener('change', updateCompare); });
    updateCompare();
  }

  // Mobile fields: keep digits only, at most 10.
  document.querySelectorAll('input[name="mobile"]').forEach(function (field) {
    field.addEventListener('input', function () {
      field.value = field.value.replace(/\D/g, '').slice(0, 10);
    });
  });

  // Admin form: validity applies to prepaid, add-on SIMs to postpaid.
  var type = document.getElementById('planType');
  if (type) {
    var validity = document.getElementById('validityDays');
    var addOns = document.getElementById('addOnSims');
    var sync = function () {
      var prepaid = type.value === 'Prepaid';
      validity.disabled = !prepaid;
      addOns.disabled = prepaid;
    };
    type.addEventListener('change', sync);
    sync();
  }
});
