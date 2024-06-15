package com.pliteratura.proyecto.menu;

import com.pliteratura.proyecto.modelo.Author;
import com.pliteratura.proyecto.modelo.Book;
import com.pliteratura.proyecto.modelo.BookData;
import com.pliteratura.proyecto.modelo.Results;
import com.pliteratura.proyecto.repositorio.AuthorRepository;
import com.pliteratura.proyecto.repositorio.BookRepository;
import com.pliteratura.proyecto.servicio.ConsumptionAPI;
import com.pliteratura.proyecto.servicio.ConvertData;

import javax.swing.JOptionPane;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class menu {
    private ConvertData convertData = new ConvertData();
    private ConsumptionAPI consumptionApi = new ConsumptionAPI();
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    List<Book> books;
    List<Author> authors;

    public menu(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void mostrarMenu() {
        final var menu = """
                \n\t**** Por favor, selecciona una opción ****
                \t1 - Buscar libro por título
                \t2 - Listar libros registrados
                \t3 - Listar autores registrados
                \t4 - Listar autores vivos en un año dado
                \t5 - Listar libros por idioma
                \n\t0 - Salir
                """;

        var opcion = -1;
        while (opcion != 0) {
            String input = JOptionPane.showInputDialog(menu);
            if (input == null) {
                break; // Salir si el usuario cancela el diálogo
            }
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Opción inválida, por favor, intenta de nuevo");
                continue;
            }

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAno();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Cerrando aplicación...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida, por favor, intenta de nuevo");
                    break;
            }
        }
    }

    private void buscarLibroPorTitulo() {
        String inTitle = JOptionPane.showInputDialog("Buscar libro por título...Por favor, introduce el título:");
        if (inTitle == null || inTitle.isEmpty()) {
            return;
        }
        var json = consumptionApi.getData(inTitle.replace(" ", "%20"));
        var data = convertData.getData(json, Results.class);
        if (data.results().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Libro no encontrado");
        } else {
            BookData bookData = data.results().getFirst();
            Book book = new Book(bookData);
            Author author = new Author().getFirstAuthor(bookData);
            guardarDatos(book, author);
        }
    }

    private void guardarDatos(Book book, Author author) {
        Optional<Book> bookFound = bookRepository.findByTitleContains(book.getTitle());
        if (bookFound.isPresent()) {
            JOptionPane.showMessageDialog(null, "Este libro ya está registrado");
        } else {
            try {
                bookRepository.save(book);
                JOptionPane.showMessageDialog(null, "Libro registrado");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }

        Optional<Author> authorFound = authorRepository.findByNameContains(author.getName());
        if (authorFound.isPresent()) {
            JOptionPane.showMessageDialog(null, "Este autor ya está registrado");
        } else {
            try {
                authorRepository.save(author);
                JOptionPane.showMessageDialog(null, "Autor registrado");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }

    private void listarLibrosRegistrados() {
        StringBuilder sb = new StringBuilder("Listar libros registrados\n---------------------\n");
        books = bookRepository.findAll();
        books.stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .forEach(book -> sb.append(book).append("\n"));
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private void listarAutoresRegistrados() {
        StringBuilder sb = new StringBuilder("Listar autores registrados\n-----------------------\n");
        authors = authorRepository.findAll();
        authors.stream()
                .sorted(Comparator.comparing(Author::getName))
                .forEach(author -> sb.append(author).append("\n"));
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private void listarAutoresVivosEnAno() {
        String input = JOptionPane.showInputDialog("Listar autores vivos en un año dado...Por favor, introduce el año:");
        if (input == null || input.isEmpty()) {
            return;
        }
        try {
            Integer year = Integer.valueOf(input);
            authors = authorRepository
                    .findByBirthYearLessThanEqualAndDeathYearGreaterThanEqual(year, year);
            if (authors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Autores vivos no encontrados");
            } else {
                StringBuilder sb = new StringBuilder();
                authors.stream()
                        .sorted(Comparator.comparing(Author::getName))
                        .forEach(author -> sb.append(author).append("\n"));
                JOptionPane.showMessageDialog(null, sb.toString());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Año inválido");
        }
    }

    private void listarLibrosPorIdioma() {
        StringBuilder sb = new StringBuilder("Listar libros por idioma\n----------------------\n");
        sb.append("""
                \n\t---- Por favor, selecciona un idioma ----
                \ten - Inglés
                \tes - Español
                \tfr - Francés
                \tpt - Portugués
                """);
        String lang = JOptionPane.showInputDialog(sb.toString());
        if (lang == null || lang.isEmpty()) {
            return;
        }
        books = bookRepository.findByLanguageContains(lang);
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Libros en el idioma seleccionado no encontrados");
        } else {
            StringBuilder result = new StringBuilder();
            books.stream()
                    .sorted(Comparator.comparing(Book::getTitle))
                    .forEach(book -> result.append(book).append("\n"));
            JOptionPane.showMessageDialog(null, result.toString());
        }
    }
}
