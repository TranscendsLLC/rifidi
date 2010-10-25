#!/usr/bin/env python

# James Percent
# james.percent@gmail.com

import ply.lex as lex
import ply.yacc as yacc

class Parser:
    tokens = ()
    precedence = ()
    def __init__(self, **kw):
        #self.debug = kw.get('debug', 0)
        self.names = { }
        try:
            modname = os.path.split(os.path.splitext(__file__)[0])[1] + \
            "_" + self.__class__.__name__
        except:
            modname = "parser"+"_"+self.__class__.__name__
        #self.debugfile = modname + ".dbg"
        #self.tabmodule = modname + "_" + "parsetab"
        #print self.debugfile, self.tabmodule

        lex.lex(module=self)#, debug=self.debug)
        yacc.yacc(module=self)#,
                  #debug=self.debug,
                  #debugfile=self.debugfile,
                  #tabmodule=self.tabmodule)
    def run_parser(self, s):
        #print s
        yacc.parse(s)
