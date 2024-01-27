# Contributing to Web Tools Common

Thank you for your interest in this project.  Don't forget to sign your Eclipse Contributor Agreement before proposing any changes.

In addition to the changes you're proposing, you should be familiar with Git and using SSH keys with Git.

If you're seeing this repository somewhere other than [this GitHub repository](https://github.com/eclipse-wtp-common/webtools.common), it's not the authoritative version used for our releases every quarter. Opening pull requests or submitting changes there will be of no use.

Hints for pull requests:
- You can also run the Maven build, supplying using at least Tycho 4.0.1 for the values in `-Dtycho-extras.version=${tychoVersion} -Dtycho.version=${tychoVersion}` and profile `bree-libs` with `-Pbree-libs`.
- Please refer the GitHub issue in your commit message (e.g #1). If you're fixing an old bug [from Bugzilla](https://bugs.eclipse.org/bugs/), start your commit messages first line with the word "bug" and the bug number, e.g. Bug 12345 to refer to bug 12345 to make it more understandable in the eventual git history.

## Eclipse Development Process

This Eclipse Foundation open project is governed by the Eclipse Foundation
Development Process and operates under the terms of the Eclipse IP Policy.

* https://eclipse.org/projects/dev_process
* https://www.eclipse.org/org/documents/Eclipse_IP_Policy.pdf

## Eclipse Contributor Agreement

In order to be able to contribute to Eclipse Foundation projects you must
electronically sign the Eclipse Contributor Agreement (ECA).

* http://www.eclipse.org/legal/ECA.php

The ECA provides the Eclipse Foundation with a permanent record that you agree
that each of your contributions will comply with the commitments documented in
the Developer Certificate of Origin (DCO). Having an ECA on file associated with
the email address matching the "Author" field of your contribution's Git commits
fulfills the DCO's requirement that you sign-off on your contributions.

For more information, please see the Eclipse Committer Handbook:
https://www.eclipse.org/projects/handbook/#resources-commit

## Contact

Contact the project developers via the project's "dev" list.

* https://accounts.eclipse.org/mailing-list/wtp-dev
