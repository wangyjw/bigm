#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
# FILE: Makefile
# AUTHOR: Michael Baldamus
# DESCRIPTION: makefile for the Java interface for ltl2ba            
#%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

# Copyright (C) 2002-2004 Michael Baldamus
# 
# This file belongs to the Java interface for ltl2ba (JLtl2ba).
# 
# JLtl2ba is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2, or (at your option)
# any later version.
# 
# JLtl2ba is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with JLtl2ba; see the file COPYING.  If not, write to
# the Free Software Foundation, 59 Temple Place - Suite 330,
# Boston, MA 02111-1307, USA.  

SHELL = /bin/sh

JPRIMARYSOURCENAMES = \
  LowLevel \
  HighLevel \
  Ltl2baException 
JCCSOURCENAME = Parser
JPARSERDERIVEDSOURCENAMES = \
  Parser \
  ParseException \
  ParserConstants \
  ParserTokenManager \
  Token \
  TokenMgrError \
  SimpleCharStream
INTERNALCLASSNAMES = \
  LowLevel\$$Error \
  HighLevel\$$Callback \
  HighLevel\$$Input \
  HighLevel\$$Input\$$Binary \
  HighLevel\$$Input\$$Unary \
  HighLevel\$$Input\$$Constant \
  HighLevel\$$Input\$$Release \
  HighLevel\$$Input\$$Until \
  HighLevel\$$Input\$$Always \
  HighLevel\$$Input\$$Eventually \
  HighLevel\$$Input\$$Next \
  HighLevel\$$Input\$$Equivalence \
  HighLevel\$$Input\$$Implication \
  HighLevel\$$Input\$$Disjunction \
  HighLevel\$$Input\$$Conjunction \
  HighLevel\$$Input\$$Negation \
  HighLevel\$$Input\$$Truthhood \
  HighLevel\$$Input\$$Falsehood \
  HighLevel\$$CallersSubstitute \
  HighLevel\$$CallersSubstitute\$$NeverClaim \
  HighLevel\$$CallersSubstitute\$$Bundle \
  HighLevel\$$CallersSubstitute\$$Transition \
  HighLevel\$$CallersSubstitute\$$State \
  HighLevel\$$CallersSubstitute\$$Guard \
  HighLevel\$$CallersSubstitute\$$Bundle\$$WithTransitions \
  HighLevel\$$CallersSubstitute\$$Bundle\$$WithoutTransitions \
  HighLevel\$$CallersSubstitute\$$State\$$AcceptInit \
  HighLevel\$$CallersSubstitute\$$State\$$AcceptSj \
  HighLevel\$$CallersSubstitute\$$State\$$AcceptAll \
  HighLevel\$$CallersSubstitute\$$State\$$TiInit \
  HighLevel\$$CallersSubstitute\$$State\$$TiSj \
  HighLevel\$$CallersSubstitute\$$Guard\$$Disjunction \
  HighLevel\$$CallersSubstitute\$$Guard\$$Conjunction \
  HighLevel\$$CallersSubstitute\$$Guard\$$Negation \
  HighLevel\$$CallersSubstitute\$$Guard\$$Truthhood \
  HighLevel\$$CallersSubstitute\$$Guard\$$Atom
PKGNAME = jltl2ba
PKGPREFIX = jltl2ba
PKGPATH = jltl2ba

CLASSINSTALLDIR = $(DEVEL)/lib/$(PKGPATH)

JH = /usr/local/bin/javah 
JHFLAGS = -jni

JC = /usr/local/bin/javac

JCC = /usr/local/bin/javacc

JSOURCESUFFIX = java
JCCSOURCESUFFIX = jj
CLASSSUFFIX = class
BACKUPSUFFIX = ~
SOPREFIX = lib
JARSUFFIX = jar

JCCSOURCE = $(JCCSOURCENAME).$(JCCSOURCESUFFIX) 
JSOURCENAMES = $(JPRIMARYSOURCENAMES) $(JPARSERDERIVEDSOURCENAMES)
JPRIMARYSOURCES = $(patsubst %, %.$(JSOURCESUFFIX), $(JPRIMARYSOURCENAMES))
JPARSERDERIVEDSOURCES = $(patsubst %, %.$(JSOURCESUFFIX), $(JPARSERDERIVEDSOURCENAMES))
JSOURCES = $(JPRIMARYSOURCES) $(JPARSERDERIVEDSOURCES)
TOPLEVELCLASSES = $(patsubst %, %.$(CLASSSUFFIX), $(JSOURCENAMES))
CLASSES = $(patsubst %, %.$(CLASSSUFFIX), $(JSOURCENAMES) $(INTERNALCLASSNAMES))

all : $(TOPLEVELCLASSES)

$(TOPLEVELCLASSES) : $(JPRIMARYSOURCES) $(JCCSOURCE)

%.$(CLASSSUFFIX) :
	$(JCC) $(JCCSOURCE)
	$(JC) $(JSOURCES)

install : 
	for file in $(CLASSES); do \
	    cp -f $$file $(CLASSINSTALLDIR); \
	done

uninstall :
	for file in $(CLASSES); do \
	    rm -f $(CLASSINSTALLDIR)/$$file; \
	done

clean : 
	rm -f $(JPARSERDERIVEDSOURCES)
	rm -f $(CLASSES)
	rm -f *$(BACKUPSUFFIX)

realclean : clean
	rm -f *.$(CLASSSUFFIX)
	rm -f $(CLASSINSTALLDIR)/*
