package com.sonymathew.course.apis.libraryapis.author;


import javax.persistence.*;

import com.sonymathew.course.apis.libraryapis.book.BookEntity;
import com.sonymathew.course.apis.libraryapis.model.common.Gender;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AUTHOR")
public class AuthorEntity {

    @Column(name = "Author_Id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorId_generator")
    @SequenceGenerator(name="authorId_generator", sequenceName = "author_sequence", allocationSize = 50)
    private int authorId;

    @Column(name = "First_Name")
    private String firstName;

    @Column(name = "Last_Name")
    private String lastName;

    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;

    @Column(name = "Gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    
    // defining many-to-many relationship couterpart of BookEntity ( there we defined a HashSet for AuthorEntity
    @ManyToMany(fetch=FetchType.LAZY,
    		    cascade = CascadeType.ALL,
    		    mappedBy = "authors")
    private Set<BookEntity> books = new HashSet<>();
    
    public AuthorEntity() {
    }

    public AuthorEntity(String firstName, String lastName, LocalDate dateOfBirth, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public AuthorEntity(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }  
    
    public int getAuthorId() {
        return authorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
