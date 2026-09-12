sudo kill -9 $(sudo lsof -t -i:4800)
cd frontend
npm install
nohup npm start &
