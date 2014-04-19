/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this distribution, and is
 * available at http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors: antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.services;

import java.util.Set;

import net.sf.gazpachoquest.domain.core.QuestionnairDefinition;
import net.sf.gazpachoquest.domain.core.Research;
import net.sf.gazpachoquest.domain.user.User;

public interface ResearchService extends PersistenceService<Research> {

    Research save(Research research, Set<QuestionnairDefinition> questionnairDefinitions, Set<User> respondents);

}