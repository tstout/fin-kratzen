# fin-kratzen

Process data from your bank account.

## Motivation
I hate having to login to my bank account every day to look at the state of my finances.
Many of the personal finance services available are nice, but I cringe at the thought 
of handing them my banking credentials. If I want to aggregate/store/process/query/alert on 
my banking data without releasing my account credentials to a third party, I must do it myself.

## Functionality
- Download bank transactions hourly (via OFX)
- Store the transactions in a database (H2)
- Backup the database to the cloud daily
- Send a daily email showing current balance and top 10 transactions

I have been running this on an old Raspberry Pi since 2015.

I thought I would implement a UI for this. However, over the years I have found simply
having the transaction data available in a relational DB is sufficient.
I have found exceptional utility in having years of my banking data available in an environment easily queried via SQL.
## Commentary on OFX
The Open Finanacial Exchange protocol is no picnic, but it 
beats web scraping. Most banks make it difficult to access your data via this protocol as a retail customer, at least in the US.

This code's interaction with OFX is rather specific to my bank.
However, the template used [here](https://github.com/tstout/fin-kratzen/blob/61cf856b4de0bfd5fca1481865ebab0fbe18752d/src/kratzen/boa_ofx.clj#L1) can likely be tweaked to your needs.

There are many obtuse parameters in the OFX protocol that are specific to each bank. A good starting point to determine
parameters needed to access your bank can be found [here](http://www.ofxhome.com/index.php/home/directory)


## Why the crazy name?
Kratzen in German can mean scrape, and a few other similar things.
When I began this project, I was doing a web scrape to gather the data. This has obvious downsides, like constanatly failing afer your bank makes the slightest change to the browser UI. The fin
component of finkratzen is simply 'financial', thus finkratzen:
financial scrape.



## License

The MIT License (MIT)

Copyright (c) 2022 Todd Stout

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