package com.kyddaniel.QuizAppQuestionMicroservice.service;

import com.kyddaniel.QuizAppQuestionMicroservice.dao.QuestionDao;
import com.kyddaniel.QuizAppQuestionMicroservice.model.Question;
import com.kyddaniel.QuizAppQuestionMicroservice.model.QuestionWrapper;
import com.kyddaniel.QuizAppQuestionMicroservice.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionDao.save(question);
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return new ResponseEntity<>("not created", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String category, int numQuestions) {
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(category, numQuestions);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionsIDs) {
        List<QuestionWrapper> wrappers = new ArrayList<>();

        for (int id : questionsIDs) {
            Optional<Question> question = questionDao.findById(id);
            question.ifPresent(value -> wrappers.add(new QuestionWrapper(value)));
        }

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {

        int score = 0;

        for (Response response : responses) {
            Optional<Question> question = questionDao.findById(response.getId());
            if (question.isPresent() && response.getResponse().equals(question.get().getCorrectAnswer()))
                score++;
        }

        return new ResponseEntity<>(score, HttpStatus.OK);
    }
}
