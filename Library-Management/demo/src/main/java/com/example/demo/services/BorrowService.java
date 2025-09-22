package com.example.demo.services;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Book;
import com.example.demo.entities.Borrower;
import com.example.demo.entities.Membership;
import com.example.demo.entities.Panalty;
import com.example.demo.entities.Transaction;
import com.example.demo.entities.Users;
import com.example.demo.repositories.BookRepository;
import com.example.demo.repositories.BorrowerRepository;
import com.example.demo.repositories.MembershipRepository;
import com.example.demo.repositories.PenaltyRepository;
import com.example.demo.repositories.TransactionRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class BorrowService {

    @Autowired
    private UserRepository userRepository;   
    @Autowired
    private MembershipRepository membershipRepository; 
    @Autowired
    private PenaltyRepository penaltyRepository; 
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;
    @Autowired
    private BookRepository bookRepository;
    
    
    public boolean canBorrowBook(String userId) {

        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false; 
        }

        Users user = userOpt.get();
        Membership membership = user.getMembership();
        if (membership == null) {
            return false; 
        }


        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(membership.getStartDate()) || currentDate.isAfter(membership.getEndDate())) {
            return false; 
        }


        List<Panalty> penalties = penaltyRepository.getPenaltiesByUserId(userId);
        for (Panalty penalty : penalties) {
            if (!penalty.isPaid()) {
                return false; 
            }
        }

        Optional<Borrower> borrowerOpt = borrowerRepository.findById(userId);
        int activeBorrowCount = 0;

        
        if (borrowerOpt.isPresent()) {
            Borrower borrower = borrowerOpt.get();
            activeBorrowCount = borrower.getActiveBorrowCount();
        }

        int maxBooksAllowed = Integer.parseInt(membership.getMaxBooks());
        if (activeBorrowCount >= maxBooksAllowed) {
            return false; 
        }

        
        return true;
        
    }

    
    public ResponseEntity<Map<String, Object>> borrowBook(String userId, String bookId) {
        // 1. Check if the user exists
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "User not found."));
        }
        Users user = userOpt.get();

        // 2. Validate if the user can borrow a book (e.g., penalties, membership, book limit)
        if (!canBorrowBook(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("message", "User cannot borrow the book due to active penalties, no membership, or book limit reached."));
        }

        // 3. Check if the book exists
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Book not found."));
        }
        Book book = bookOpt.get();

        // 4. Check book availability
        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "No available copies of the book."));
        }

        // 5. Create and save the transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setBorrowDate(LocalDate.now());

        // Fetch the membership to determine the due date
        Membership membership = user.getMembership();
        int borrowDurationDays = membership.getBorrowDuration();
        LocalDate dueDate = LocalDate.now().plusDays(borrowDurationDays);
        transaction.setDueDate(dueDate);
        transactionRepository.save(transaction);

        // 6. Update book availability
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 7. Update the Borrower entity (borrow table)
        Optional<Borrower> borrowerOpt = borrowerRepository.findById(userId);
        Borrower borrower = borrowerOpt.orElseGet(() -> {
            Borrower newBorrower = new Borrower();
            newBorrower.setUser(user);
            newBorrower.setActiveBorrowCount(0);
            return newBorrower;
        });
        borrower.setActiveBorrowCount(borrower.getActiveBorrowCount() + 1); // Increment active borrow count
        borrowerRepository.save(borrower);

        // 8. Build the response body with detailed information
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book borrowed successfully.");
        response.put("userDetails", Map.of(
                "userId", user.getUserId(),
                "userName", user.getUsername(),
                "fullname", user.getFullname(),
                "email", user.getEmail(),
                "activeBorrowCount", borrower.getActiveBorrowCount()
        ));
        response.put("bookDetails", Map.of(
                "bookId", book.getBookId(),
                "title", book.getTitle(),
                "author", book.getAuthor(),
                "category", book.getCategory()
        ));
        response.put("transactionDetails", Map.of(
                "transactionId", transaction.getTransactionId(),
                "borrowDate", transaction.getBorrowDate(),
                "dueDate", dueDate
        ));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}

