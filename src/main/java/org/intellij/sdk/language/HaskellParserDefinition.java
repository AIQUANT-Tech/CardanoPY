package org.intellij.sdk.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.sdk.language.parser.HaskellParser;
import org.intellij.sdk.language.psi.HaskellFile;
import org.intellij.sdk.language.psi.HaskellTokenSets;
import org.intellij.sdk.language.psi.HaskellTypes;
import org.jetbrains.annotations.NotNull;

public final class HaskellParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(HaskellLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new HaskellLexerAdapter();
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return HaskellTokenSets.COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public PsiParser createParser(final Project project) {
        return new HaskellParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new HaskellFile(viewProvider);
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return HaskellTypes.Factory.createElement(node);
    }

}