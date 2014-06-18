package net.sf.gazpachoquest.questionnair.support;

import static org.fest.assertions.api.Assertions.assertThat;
import net.sf.gazpachoquest.domain.support.QuestionnairElement;
import net.sf.gazpachoquest.dto.PageMetadataDTO;
import net.sf.gazpachoquest.services.QuestionGroupService;
import net.sf.gazpachoquest.services.QuestionService;
import net.sf.gazpachoquest.test.dbunit.support.ColumnDetectorXmlDataSetLoader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/jpa-test-context.xml", "classpath:/datasource-test-context.xml",
        "classpath:/services-context.xml", "classpath:/components-context.xml", "classpath:/questionnair-context.xml",
        "classpath:/facades-context.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("PageMetadataCreatorTest-dataset.xml")
@DatabaseTearDown("PageMetadataCreatorTest-dataset.xml")
@DbUnitConfiguration(dataSetLoader = ColumnDetectorXmlDataSetLoader.class)
public class PageMetadataCreatorTest {

    @Autowired
    private PageMetadataCreator pageMetadataCreator;

    @Autowired
    private QuestionGroupService questionGroupService;

    @Autowired
    private QuestionService questionService;

    @Test
    public void createForQuestionGroupTest() {
        QuestionnairElement questionnairElement = questionGroupService.findOne(9);
        PageMetadataDTO metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isFirst()).isTrue();

        questionnairElement = questionGroupService.findOne(10);
        metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isLast()).isFalse();
        assertThat(metadata.isFirst()).isFalse();

        questionnairElement = questionGroupService.findOne(11);
        metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isLast()).isTrue();

    }

    @Test
    public void createForQuestionTest() {
        QuestionnairElement questionnairElement = questionService.findOne(12);
        PageMetadataDTO metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isFirst()).isTrue();

        questionnairElement = questionService.findOne(30);
        metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isLast()).isFalse();
        assertThat(metadata.isFirst()).isFalse();

        questionnairElement = questionService.findOne(50);
        metadata = pageMetadataCreator.create(questionnairElement);
        assertThat(metadata.isLast()).isTrue();
    }
}