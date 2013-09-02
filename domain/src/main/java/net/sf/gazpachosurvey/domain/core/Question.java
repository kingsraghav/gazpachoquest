package net.sf.gazpachosurvey.domain.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import net.sf.gazpachosurvey.domain.i18.QuestionTranslation;
import net.sf.gazpachosurvey.domain.support.AbstractPersistable;
import net.sf.gazpachosurvey.types.Language;
import net.sf.gazpachosurvey.types.QuestionType;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.springframework.util.Assert;

@Entity
public class Question extends AbstractPersistable<Integer> {

    private static final long serialVersionUID = -4372634574851905803L;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne
    private Question parent;

    @ManyToOne
    private Page page;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderColumn(name = "order_in_subquestions")
    private List<Question> subquestions;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_in_question")
    @JoinFetch(JoinFetchType.OUTER)
    private List<Answer> answers;

    private String title;

    boolean isRequired;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    private Language language;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "language")
    private Map<Language, QuestionTranslation> translations;

    public Question() {
        super();
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public List<Question> getSubquestions() {
        if (subquestions == null) {
            subquestions = new ArrayList<>();
        }
        return subquestions;
    }

    public void setSubquestions(List<Question> subquestions) {
        this.subquestions = subquestions;
    }

    public Question getParent() {
        return parent;
    }

    public void setParent(Question parent) {
        this.parent = parent;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Answer> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Map<Language, QuestionTranslation> getTranslations() {
        if (translations == null) {
            translations = new HashMap<>();
        }
        return translations;
    }

    public void setTranslations(Map<Language, QuestionTranslation> translations) {
        this.translations = translations;
    }

    public void setTranslation(Language language, String text) {
        QuestionTranslation translation = new QuestionTranslation();
        translation.setText(text);
        translation.setQuestion(this);
        translation.setLanguage(language);
        getTranslations().put(language, translation);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void addAnswer(Answer answer) {
        getAnswers().add(answer);
        answer.setQuestion(this);
    }

    public void addSubquestion(Question subquestion) {
        Assert.notNull(subquestion);
        subquestion.setLanguage(language);
        getSubquestions().add(subquestion);
        subquestion.setParent(this);
    }

}
