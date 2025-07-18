package com.beyond.basic.b2_board.author.repository;

import com.beyond.basic.b2_board.author.domain.Author;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class AuthorMemoryRepository {
    private List<Author> authorList = new ArrayList<>();
    public static Long id = 1L;

    public void save(Author author){
        this.authorList.add(author);
        id++;
    }

    public List<Author> findAll(){

        return authorList;
    }

    public Optional<Author> findById(Long id){
        return authorList.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public Optional<Author> findByEmail(String email){
        return authorList.stream().filter((a)->a.getEmail().equals(email)).findFirst();
    }

    public void delete(Long id){
        //id 값으로 요소의 index값을 찾아 삭제
        int idx=0;
        boolean check = false;
        for(int i=0; i<authorList.size(); i++){
            if(authorList.get(i).getId().equals(id)){
                authorList.remove(i);
                check=true;
                break;
            }
        }

        if(!check){
            new NoSuchElementException();
        }

    }


}