package net.sf.gazpachoquest.questionnair.resolver;

import java.util.List;

import net.sf.gazpachoquest.domain.core.BrowsedElement;
import net.sf.gazpachoquest.domain.core.BrowsedQuestionGroup;
import net.sf.gazpachoquest.domain.core.QuestionGroup;
import net.sf.gazpachoquest.domain.core.Questionnair;
import net.sf.gazpachoquest.domain.core.QuestionnairDefinition;
import net.sf.gazpachoquest.qbe.support.SearchParameters;
import net.sf.gazpachoquest.repository.BrowsedElementRepository;
import net.sf.gazpachoquest.repository.QuestionGroupRepository;
import net.sf.gazpachoquest.types.BrowsingAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("GroupByGroupResolver")
public class GroupByGroupResolverImpl implements QuestionnairElementResolver {

    private static final Integer INITIAL_POSITION = 0;

    private static final Logger logger = LoggerFactory.getLogger(GroupByGroupResolverImpl.class);

    @Autowired
    private BrowsedElementRepository browsedElementService;

    @Autowired
    private QuestionGroupRepository questionGroupService;

    @Override
    public QuestionGroup resolveFor(final Questionnair questionnair, final BrowsingAction action) {
        QuestionnairDefinition questionnairDefinition = questionnair.getQuestionnairDefinition();
        int questionnairDefinitionId = questionnairDefinition.getId();
        int questionnairId = questionnair.getId();
        logger.debug("Finding {} QuestionGroup for questionnair {}", action.toString(), questionnairId);

        BrowsedElement browsedElement = browsedElementService.findLast(questionnairId);
        QuestionGroup questionGroup = null;
        BrowsedQuestionGroup lastBrowsedQuestionGroup = null;

        if (browsedElement == null) { // First time entering the
                                      // questionnairDefinition
            questionGroup = findFirstQuestionGroup(questionnairDefinitionId);
            lastBrowsedQuestionGroup = BrowsedQuestionGroup.with().questionnair(questionnair)
                    .questionGroup(questionGroup).last(Boolean.TRUE).build();
            browsedElementService.save(lastBrowsedQuestionGroup);
            return questionGroup;
        } else {
            if (browsedElement instanceof BrowsedQuestionGroup) {
                lastBrowsedQuestionGroup = (BrowsedQuestionGroup) browsedElement;
            } else {
                List<BrowsedElement> browsedElements = browsedElementService.findByExample(BrowsedElement.withProps()
                        .questionnair(Questionnair.with().id(questionnairId).build()).build(), new SearchParameters());
                browsedElementService.deleteInBatch(browsedElements);
                questionGroup = findFirstQuestionGroup(questionnairDefinitionId);
                lastBrowsedQuestionGroup = BrowsedQuestionGroup.with().questionnair(questionnair)
                        .questionGroup(questionGroup).last(Boolean.TRUE).build();
                browsedElementService.save(lastBrowsedQuestionGroup);
                return questionGroup;
            }
        }

        if (BrowsingAction.ENTERING.equals(action)) {
            questionGroup = lastBrowsedQuestionGroup.getQuestionGroup();
        } else {
            if (BrowsingAction.FORWARD.equals(action)) {
                questionGroup = findNextQuestionGroup(questionnairDefinitionId, questionnair, lastBrowsedQuestionGroup);
            } else {// BACKWARD
                questionGroup = findPreviousQuestionGroup(questionnairDefinitionId, questionnair,
                        lastBrowsedQuestionGroup);
            }
            if (questionGroup != null) { // Prevent that questions are still in
                                         // range.
                lastBrowsedQuestionGroup.setLast(Boolean.FALSE);
                browsedElementService.save(lastBrowsedQuestionGroup);
            }
        }
        return questionGroup;
    }

    private QuestionGroup findFirstQuestionGroup(int questionnairDefinitionId) {
        return questionGroupService.findOneByPositionInQuestionnairDefinition(questionnairDefinitionId,
                INITIAL_POSITION);
    }

    private QuestionGroup findNextQuestionGroup(final Integer questionnairDefinitionId,
            final Questionnair questionnair, final BrowsedQuestionGroup lastBrowsedElement) {

        BrowsedElement nextBrowsedElement = browsedElementService.findNext(questionnair.getId(),
                lastBrowsedElement.getCreatedDate());

        BrowsedQuestionGroup nextBrowsedQuestionGroup = null;
        QuestionGroup next = null;

        if (nextBrowsedElement == null) {
            Integer position = questionGroupService.findPositionInQuestionnairDefinition(lastBrowsedElement
                    .getQuestionGroup().getId());
            next = questionGroupService.findOneByPositionInQuestionnairDefinition(questionnairDefinitionId,
                    position + 1);
            if (next == null) { // The respondent has reached the last question
                                // group
                return next;
            }
            // Mark next element as last browsed.
            nextBrowsedQuestionGroup = BrowsedQuestionGroup.with().questionnair(questionnair).questionGroup(next)
                    .last(Boolean.TRUE).build();
        } else {
            Assert.isInstanceOf(BrowsedQuestionGroup.class, nextBrowsedElement);
            nextBrowsedQuestionGroup = (BrowsedQuestionGroup) nextBrowsedElement;
            next = nextBrowsedQuestionGroup.getQuestionGroup();
            nextBrowsedQuestionGroup.setLast(Boolean.TRUE);
        }
        browsedElementService.save(nextBrowsedQuestionGroup);
        return next;
    }

    private QuestionGroup findPreviousQuestionGroup(final int questionnairDefinitionId,
            final Questionnair questionnair, final BrowsedQuestionGroup lastBrowsedElement) {
        BrowsedElement previousBrowsedElement = browsedElementService.findPrevious(questionnair.getId(),
                lastBrowsedElement.getCreatedDate());
        if (previousBrowsedElement == null) {
            return null;
        }
        Assert.isInstanceOf(BrowsedQuestionGroup.class, previousBrowsedElement);

        BrowsedQuestionGroup previousBrowsedQuestionGroup = (BrowsedQuestionGroup) previousBrowsedElement;
        previousBrowsedQuestionGroup.setLast(Boolean.TRUE);
        browsedElementService.save(previousBrowsedQuestionGroup);
        return previousBrowsedQuestionGroup.getQuestionGroup();
    }
}
