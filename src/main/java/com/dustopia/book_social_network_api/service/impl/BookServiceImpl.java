package com.dustopia.book_social_network_api.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dustopia.book_social_network_api.exception.BookUnavailableException;
import com.dustopia.book_social_network_api.exception.CloudinaryUploadException;
import com.dustopia.book_social_network_api.exception.PermissionDeniedAccessException;
import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.BookMapper;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import com.dustopia.book_social_network_api.repository.BookTransactionRepository;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.BookService;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookTransactionRepository bookTransactionRepository;

    private final BookMapper bookMapper;

    private final Cloudinary cloudinary;

    private final Drive drive;

    @Value("${google-api.drive.folder-id}")
    private String FOLDER_ID;

    @Override
    public BookDto addBook(BookRequest bookRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book newBook = bookMapper.toBook(bookRequest);
        newBook.setUser(user);
        bookRepository.save(newBook);
        return bookMapper.toBookDto(newBook);
    }

    @Override
    public BookDto getBookById(Long id, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!book.isShareable()) {
            if (!user.getId().equals(book.getUser().getId()) && !user.getRole().equals("ADMIN")) {
                throw new BookUnavailableException("Book is currently unavailable");
            }
        }
        return bookMapper.toBookDto(book);
    }

    @Override
    public PageData<BookDto> findAllDisplayableBooks(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public PageData<BookDto> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllBooksByOwner(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public PageData<BookDto> findAllPurchasedBooks(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransaction> books = bookTransactionRepository.findAllPurchasedBooks(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public BookDto updateBookById(Long id, BookRequest bookRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId())) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        if (bookRequest.title() != null) book.setTitle(bookRequest.title());
        if (bookRequest.author() != null) book.setAuthor(bookRequest.author());
        if (bookRequest.synopsis() != null) book.setSynopsis(bookRequest.synopsis());
        book.setShareable(bookRequest.isShareable());
        bookRepository.save(book);
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto deleteBookById(Long id, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId()) && !user.getRole().equals("ADMIN")) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        bookRepository.deleteById(id);
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto uploadBookCover(Long id, MultipartFile file, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId())) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        try {
            Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "book-covers"));
            String bookCoverUrl = (String) result.get("url");
            book.setBookCoverUrl(bookCoverUrl);
            bookRepository.save(book);
            return bookMapper.toBookDto(book);
        } catch (IOException e) {
            throw new CloudinaryUploadException("Image uploader service occurred error " + e.getMessage());
        }
    }

    @Override
    public BookDto uploadBook(Long id, MultipartFile file, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId())) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        try {
            if (book.getUrl() != null) {
                drive.files().delete(extractGGDriveBookId(book.getUrl())).execute();
            }
            File fileMetadata = new File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList(FOLDER_ID));
            InputStream inputStream = file.getInputStream();
            File driveFile = drive.files().create(fileMetadata, new InputStreamContent("application/pdf", inputStream))
                    .setFields("id")
                    .execute();
            book.setUrl("https://drive.google.com/file/d/" + driveFile.getId());
            bookRepository.save(book);
            return bookMapper.toBookDto(book);
        } catch (IOException exception) {
            throw new RuntimeException("File upload invalid");
        }
    }

    @Override
    public ByteArrayResource downloadBook(Long id, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId())
                && !user.getRole().equals("ADMIN")
                && !bookTransactionRepository.isPurchased(user, book)
        ) {
            throw new PermissionDeniedAccessException("Current user don't have permission to download this book");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            drive.files().get(extractGGDriveBookId(book.getUrl())).executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public File getFileInfo(Long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        try {
            return drive.files().get(extractGGDriveBookId(book.getUrl())).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractGGDriveBookId(String bookUrl) {
        String regex = ".*/([^/?]+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(bookUrl);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

}
