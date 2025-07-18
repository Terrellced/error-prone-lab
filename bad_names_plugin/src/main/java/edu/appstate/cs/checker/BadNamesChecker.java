package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
        name = "BadNamesChecker",
        summary = "Poor-quality identifiers",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/plse-Lab/"
)
public class BadNamesChecker extends BugChecker implements
        BugChecker.IdentifierTreeMatcher,
        BugChecker.MethodInvocationTreeMatcher,
        BugChecker.MethodTreeMatcher,
        BugChecker.IfTreeMatcher {

    @java.lang.Override
    public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
        // NOTE: This matches identifier uses. Do we want to match these,
        // or just declarations?
        Name identifier = identifierTree.getName();
        return checkName(identifierTree, identifier);
    }

    //added in class
    @Override
    public Description matchIf(IfTree tree, VisitorState state){
        if(tree.getElseStatement() == null){
        return buildDescription(tree)
                .setMessage("We found an If without an else")
                .build();            
        }



        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        // NOTE: Similarly to the above, this matches method names in method
        // calls. Do we want to match these, or just declarations?
        Tree methodSelect = methodInvocationTree.getMethodSelect();

        Name identifier;

        if (methodSelect instanceof MemberSelectTree) {
            identifier = ((MemberSelectTree) methodSelect).getIdentifier();
        } else if (methodSelect instanceof IdentifierTree) {
            identifier = ((IdentifierTree) methodSelect).getName();
        } else {
            throw malformedMethodInvocationTree(methodInvocationTree);
        }

        return checkName(methodInvocationTree, identifier);
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        // MethodTree represents the definition of a method. We want to check the name of this
        // method to see if it is acceptable.

        Name methodName = methodTree.getName();

        return checkName(methodTree, methodName);

        //saving incase it doesnt build
        //return Description.NO_MATCH; 
    }

    private Description checkName(Tree tree, Name identifier) {
        // TODO: What other names are a problem? Add checks for them here...

        String name = identifier.toString();

        if (identifier.contentEquals("foo") || identifier.contentEquals("bar")
                || identifier.contentEquals("function") || identifier.contentEquals("thing")) { //add checks for bad names.
            return buildDescription(tree)
                    .setMessage(String.format("%s is a bad identifier name", identifier))
                    .build();
        }

        //length checks

        if(name.length() > 8){ //toolong
            return buildDescription(tree)
                    .setMessage(String.format("%s is too long of an identifier name", identifier))
                    .build();
        }
        
         if(name.length() < 5){ //tooshort
            return buildDescription(tree)
                    .setMessage(String.format("%s is too short of an identifier name", identifier))
                    .build();
         }

         if(name.startsWith("_")){ //tooshort
            return buildDescription(tree)
                    .setMessage(String.format("%s Shouldn't start with an underscore.", identifier))
                    .build();
         }

        //saving just incase it doesnt build
        return Description.NO_MATCH; 
    }

    private static final IllegalStateException malformedMethodInvocationTree(MethodInvocationTree tree) {
        return new IllegalStateException(String.format("Method name %s is malformed.", tree));
    }
}


