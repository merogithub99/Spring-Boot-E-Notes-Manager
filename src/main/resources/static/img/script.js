
   alert("Done")
   console.log("Hello")

    document.getElementById('categorySelect').addEventListener('change', function() {
    const select = this;
    const input = document.getElementById('newCategoryInput');
    console.log("Is this working");
    input.style.display = select.value === 'addNew' ? 'block' : 'none';
});





