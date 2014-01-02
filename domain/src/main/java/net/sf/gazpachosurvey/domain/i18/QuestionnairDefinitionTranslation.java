package net.sf.gazpachosurvey.domain.i18;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import net.sf.gazpachosurvey.domain.core.QuestionnairDefinition;
import net.sf.gazpachosurvey.domain.core.embeddables.QuestionnairDefinitionLanguageSettings;
import net.sf.gazpachosurvey.domain.support.AbstractPersistable;
import net.sf.gazpachosurvey.domain.support.Translation;
import net.sf.gazpachosurvey.domain.support.TranslationBuilder;
import net.sf.gazpachosurvey.types.Language;

@Entity
public class QuestionnairDefinitionTranslation extends AbstractPersistable implements Translation<QuestionnairDefinitionLanguageSettings> {

    private static final long serialVersionUID = -1926161817588270977L;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionnairDefinition questionnairDefinition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = true, updatable = true)
    private Language language;

    @Embedded
    private QuestionnairDefinitionLanguageSettings languageSettings;

    public QuestionnairDefinitionTranslation() {
        super();
    }

    public QuestionnairDefinition getSurvey() {
        return questionnairDefinition;
    }

    public void setSurvey(QuestionnairDefinition questionnairDefinition) {
        this.questionnairDefinition = questionnairDefinition;
    }

    @Override
    public QuestionnairDefinitionLanguageSettings getLanguageSettings() {
        if (languageSettings == null) {
            languageSettings = new QuestionnairDefinitionLanguageSettings();
        }
        return languageSettings;
    }

    @Override
    public void setLanguageSettings(QuestionnairDefinitionLanguageSettings languageSettings) {
        this.languageSettings = languageSettings;
    }

    @Override
    public Language getLanguage() {

        return language;
    }

    @Override
    public void setLanguage(Language language) {
        this.language = language;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder implements TranslationBuilder<QuestionnairDefinitionTranslation, QuestionnairDefinitionLanguageSettings> {
        private QuestionnairDefinition questionnairDefinition;
        private Language language;
        private QuestionnairDefinitionLanguageSettings languageSettings;
        private Integer id;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder questionnairDefinition(QuestionnairDefinition questionnairDefinition) {
            this.questionnairDefinition = questionnairDefinition;
            return this;
        }

        @Override
        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        @Override
        public Builder languageSettings(QuestionnairDefinitionLanguageSettings languageSettings) {
            this.languageSettings = languageSettings;
            return this;
        }

        @Override
        public QuestionnairDefinitionTranslation build() {
            QuestionnairDefinitionTranslation questionnairDefinitionTranslation = new QuestionnairDefinitionTranslation();
            questionnairDefinitionTranslation.questionnairDefinition = questionnairDefinition;
            questionnairDefinitionTranslation.language = language;
            questionnairDefinitionTranslation.languageSettings = languageSettings;
            questionnairDefinitionTranslation.setId(id);
            return questionnairDefinitionTranslation;
        }

        @Override
        public Builder translatedEntityId(Integer entityId) {
            return questionnairDefinition(QuestionnairDefinition.with().id(entityId).build());
        }

    }

    @Override
    public Integer getTranslatedEntityId() {
        // TODO Auto-generated method stub
        return null;
    }
}
