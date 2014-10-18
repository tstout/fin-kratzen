# fin-kratzen

Process data from your bank account.

## Motivation
I hate having to login to my bank account every day to look at the state of my finances.
Many of the personal finance services available can be nice, but I cringe at the thought 
of handing them my banking credentials. If I want to aggregate/store/process/query/alert on 
my banking data without releasing my account credentials to a third party, I must do it myself.

## Roadmap
This is in the early stages. I'm using it as a project to learn clojure while
implementing something that I need. The initial goal is to periodically download
financial transactions and store them in a local database. 

Eventually, some type of analytics and UI
will be implemented. I'm leaning towards clojurescript for the UI.

## License

The MIT License (MIT)

Copyright (c) 2014 Todd Stout

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.